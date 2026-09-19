// FILE: src/main/java/com/mobilewallet/service/impl/OTPServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.auth.OTPResponse;
import com.mobilewallet.entity.OTPVerification;
import com.mobilewallet.entity.User;
import com.mobilewallet.exception.InvalidOTPException;
import com.mobilewallet.exception.ResourceNotFoundException;
import com.mobilewallet.repository.OTPVerificationRepository;
import com.mobilewallet.repository.UserRepository;
import com.mobilewallet.service.OTPService;
import com.mobilewallet.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPServiceImpl implements OTPService {

    private final OTPVerificationRepository otpVerificationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Value("${app.otp.length:6}")
    private int otpLength;

    @Value("${app.otp.expiration-seconds:300}")
    private int expirationSeconds;

    @Value("${app.otp.max-attempts:3}")
    private int maxAttempts;

    @Value("${app.otp.resend-cooldown-seconds:60}")
    private int resendCooldownSeconds;

    @Value("${app.otp.dev-mode-enabled:true}")
    private boolean devModeEnabled;

    @Value("${app.otp.dev-otp-value:123456}")
    private String devOtpValue;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public OTPResponse generateAndSendOTP(String identifier, String purpose) {
        log.info("Generating OTP for identifier: {}, purpose: {}", identifier, purpose);

        // Check rate limiting
        if (isOTPRateLimited(identifier)) {
            throw new InvalidOTPException("Too many OTP requests. Please try again later.");
        }

        // Get user
        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identifier));

        // Invalidate existing OTPs
        invalidateOTP(identifier, purpose);

        // Generate OTP
        String otpCode;
        if (devModeEnabled) {
            otpCode = devOtpValue;
            log.info("DEV MODE: OTP for {} is {}", identifier, otpCode);
        } else {
            otpCode = generateSecureOTP();
        }

        // Save OTP
        OTPVerification otpVerification = OTPVerification.builder()
                .user(user)
                .otpCode(otpCode)
                .identifier(identifier)
                .purpose(purpose)
                .attempts(0)
                .verified(false)
                .expiresAt(LocalDateTime.now().plusSeconds(expirationSeconds))
                .build();

        otpVerificationRepository.save(otpVerification);

        // Send OTP via notification
        notificationService.sendOTPNotification(user, otpCode);

        return OTPResponse.builder()
                .identifier(identifier)
                .purpose(purpose)
                .expiresAt(otpVerification.getExpiresAt())
                .message("OTP sent successfully")
                .build();
    }

    @Override
    @Transactional
    public boolean verifyOTP(String identifier, String purpose, String otpCode) {
        log.info("Verifying OTP for identifier: {}, purpose: {}", identifier, purpose);

        OTPVerification otpVerification = otpVerificationRepository
                .findValidOTP(identifier, purpose, otpCode, LocalDateTime.now())
                .orElseThrow(() -> new InvalidOTPException("Invalid or expired OTP"));

        // Check attempts
        if (otpVerification.getAttempts() >= maxAttempts) {
            otpVerificationRepository.delete(otpVerification);
            throw new InvalidOTPException("Maximum OTP attempts exceeded");
        }

        // Mark as verified
        otpVerification.setVerified(true);
        otpVerificationRepository.save(otpVerification);

        // Delete after verification
        otpVerificationRepository.delete(otpVerification);

        log.info("OTP verified successfully for identifier: {}", identifier);
        return true;
    }

    @Override
    @Transactional
    public OTPResponse resendOTP(String identifier, String purpose) {
        log.info("Resending OTP for identifier: {}, purpose: {}", identifier, purpose);

        // Check if there's an existing OTP
        List<OTPVerification> existingOTPs = otpVerificationRepository
                .findUnverifiedByIdentifierAndPurpose(identifier, purpose);

        if (!existingOTPs.isEmpty()) {
            OTPVerification lastOTP = existingOTPs.get(0);
            // Check cooldown
            if (LocalDateTime.now().isBefore(lastOTP.getCreatedAt().plusSeconds(resendCooldownSeconds))) {
                throw new InvalidOTPException("Please wait before requesting a new OTP");
            }
        }

        // Generate and send new OTP
        return generateAndSendOTP(identifier, purpose);
    }

    @Override
    @Transactional
    public void invalidateOTP(String identifier, String purpose) {
        log.info("Invalidating OTP for identifier: {}, purpose: {}", identifier, purpose);
        List<OTPVerification> otps = otpVerificationRepository
                .findUnverifiedByIdentifierAndPurpose(identifier, purpose);
        otpVerificationRepository.deleteAll(otps);
    }

    @Override
    @Transactional
    @Scheduled(fixedDelay = 60000) // Run every minute
    public void cleanupExpiredOTPs() {
        log.info("Cleaning up expired OTPs");
        int deleted = otpVerificationRepository.deleteExpiredOrVerified(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Deleted {} expired/verified OTPs", deleted);
        }
    }

    @Override
    public boolean isOTPRateLimited(String identifier) {
        // Check if there are too many recent OTP requests
        LocalDateTime since = LocalDateTime.now().minusMinutes(5);
        long count = otpVerificationRepository.countRecentOTPs(identifier, since);
        return count >= 5; // Max 5 OTPs in 5 minutes
    }

    private String generateSecureOTP() {
        StringBuilder otp = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }
}