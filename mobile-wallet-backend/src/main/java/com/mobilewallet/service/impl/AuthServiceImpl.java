// FILE: src/main/java/com/mobilewallet/service/impl/AuthServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.auth.*;
import com.mobilewallet.entity.*;
import com.mobilewallet.exception.*;
import com.mobilewallet.repository.*;
import com.mobilewallet.security.JwtTokenProvider;
import com.mobilewallet.service.AuthService;
import com.mobilewallet.service.OTPService;
import com.mobilewallet.service.WalletService;
import com.mobilewallet.util.AuditUtil;
import com.mobilewallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final OTPService otpService;
    private final WalletService walletService;
    private final AuditUtil auditUtil;
    private final ReferenceNumberGenerator referenceGenerator;

    @Value("${app.guest.enabled:true}")
    private boolean guestEnabled;
    
    @Value("${app.otp.expiration-seconds:300}")
    private int otpExpirationSeconds;

 // ============================================================
    // REGISTER
    // ============================================================
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // ---------- 1. Validate uniqueness ----------
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already registered");
        }

        // ---------- 2. Validate role ----------
        User.Role role = parseRole(request.getRole());
        if (role == User.Role.ROLE_MERCHANT) {
            validateMerchantFields(request);
        }

        // ---------- 3. Create user ----------
        User user = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .role(role)
                .enabled(true)
                .locked(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created with id={}", savedUser.getId());

        // ---------- 4. Create wallet ----------
        Wallet wallet = walletService.createWallet(savedUser);

        // ---------- 5. If merchant, create merchant profile ----------
        if (role == User.Role.ROLE_MERCHANT) {
            Merchant merchant = Merchant.builder()
                    .user(savedUser)
                    .businessName(request.getBusinessName())
                    .businessType(request.getBusinessType())
                    .businessAddress(request.getBusinessAddress())
                    .businessPhone(request.getBusinessPhone())
                    .gstNumber(request.getGstNumber())
                    .verified(false)
                    .build();
            merchantRepository.save(merchant);
        }

        // ---------- 6. Send OTP for verification ----------
        OTPResponse otpResponse = otpService.generateAndSendOTP(
                savedUser.getPhone(), "REGISTRATION");

        // ---------- 7. Audit log ----------
        auditUtil.logAction("USER_REGISTRATION", savedUser,
                "User registered with username=" + savedUser.getUsername()
                        + ", role=" + savedUser.getRole());

        // ---------- 8. Build response ----------
        return RegisterResponse.builder()
                .success(true)
                .message("Registration successful. Please verify OTP to activate your account.")
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole().name())
                .requiresOTPVerification(true)
                .otpIdentifier(savedUser.getPhone())
                .otpPurpose("REGISTRATION")
                .otpExpiresInSeconds(otpExpirationSeconds)
                .wallet(RegisterResponse.WalletInfo.builder()
                        .id(wallet.getId())
                        .walletNumber(wallet.getWalletNumber())
                        .balance(wallet.getBalance().toPlainString())
                        .currency(wallet.getCurrency())
                        .status(wallet.getStatus().name())
                        .build())
                .createdAt(savedUser.getCreatedAt() != null
                        ? savedUser.getCreatedAt()
                        : LocalDateTime.now())
                .build();
    }

    // ============================================================
    // Helpers
    // ============================================================
    private User.Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return User.Role.ROLE_CONSUMER;
        }
        try {
            return User.Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentException("Invalid role: " + role);
        }
    }

    private void validateMerchantFields(RegisterRequest r) {
        if (isBlank(r.getBusinessName())) {
            throw new InvalidPaymentException("Business name is required for merchant registration");
        }
        if (isBlank(r.getBusinessType())) {
            throw new InvalidPaymentException("Business type is required for merchant registration");
        }
        if (isBlank(r.getBusinessAddress())) {
            throw new InvalidPaymentException("Business address is required for merchant registration");
        }
        if (isBlank(r.getBusinessPhone())) {
            throw new InvalidPaymentException("Business phone is required for merchant registration");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    
    // login
    
    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for: {}", request.getUsernameOrEmail());

        User user = userRepository.findByIdentifier(request.getUsernameOrEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!user.isEnabled()) {
            throw new AccountLockedException("Account is disabled");
        }

        if (user.isLocked()) {
            throw new AccountLockedException("Account is locked");
        }

        // Authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        // Save refresh token
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        auditUtil.logAction("USER_LOGIN", user, "User logged in successfully");

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");

        String refreshToken = request.getRefreshToken();
        
        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Get user from token
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate refresh token exists and is not expired
        RefreshToken storedToken = refreshTokenRepository.findValidToken(
                user.getId(), refreshToken, LocalDateTime.now())
                .orElseThrow(() -> new InvalidTokenException("Refresh token expired or invalid"));

        // Generate new tokens
        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);

        // Delete old refresh token
        refreshTokenRepository.delete(storedToken);

        // Save new refresh token
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(newRefreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("Logging out user");

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token -> {
                        refreshTokenRepository.delete(token);
                        auditUtil.logAction("USER_LOGOUT", token.getUser(), "User logged out");
                    });
        }

        SecurityContextHolder.clearContext();
    }

    @Override
    public OTPResponse generateOTP(String identifier, String purpose) {
        return otpService.generateAndSendOTP(identifier, purpose);
    }

    @Override
    public boolean verifyOTP(OTPRequest request) {
        return otpService.verifyOTP(request.getIdentifier(), request.getPurpose(), request.getOtpCode());
    }

    @Override
    public OTPResponse resendOTP(String identifier, String purpose) {
        return otpService.resendOTP(identifier, purpose);
    }

    @Override
    @Transactional
    public AuthResponse guestLogin(GuestLoginRequest request) {
        log.info("Guest login for device: {}", request.getDeviceId());

        if (!guestEnabled) {
            throw new GuestLoginDisabledException("Guest login is disabled");
        }

        String guestUsername = "guest_" + request.getDeviceId().substring(0, Math.min(8, request.getDeviceId().length()));

        User guestUser = userRepository.findByUsername(guestUsername)
                .orElseGet(() -> {
                    // Create guest user
                    User newGuest = User.builder()
                            .username(guestUsername)
                            .email(guestUsername + "@guest.mobilewallet.com")
                            .phone("0000000000")
                            .passwordHash(passwordEncoder.encode("guest" + request.getDeviceId()))
                            .firstName("Guest")
                            .lastName("User")
                            .role(User.Role.ROLE_CONSUMER)
                            .enabled(true)
                            .locked(false)
                            .build();
                    
                    User savedGuest = userRepository.save(newGuest);
                    
                    // Create wallet with zero balance
                    walletService.createWallet(savedGuest);
                    
                    return savedGuest;
                });

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(guestUser);
        String refreshToken = tokenProvider.generateRefreshToken(guestUser);

        // Save refresh token
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(guestUser)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        auditUtil.logAction("GUEST_LOGIN", guestUser, "Guest login from device: " + request.getDeviceId());

        return buildAuthResponse(guestUser, accessToken, refreshToken);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        Wallet wallet = walletRepository.findByUser_Id(user.getId())
                .orElse(null);

        AuthResponse.WalletInfo walletInfo = null;
        if (wallet != null) {
            walletInfo = AuthResponse.WalletInfo.builder()
                    .id(wallet.getId())
                    .walletNumber(wallet.getWalletNumber())
                    .balance(wallet.getBalance().toString())
                    .currency(wallet.getCurrency())
                    .status(wallet.getStatus().name())
                    .build();
        }

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .enabled(user.getEnabled())
                .locked(user.getLocked())
                .wallet(walletInfo)
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .user(userInfo)
                .build();
    }
}