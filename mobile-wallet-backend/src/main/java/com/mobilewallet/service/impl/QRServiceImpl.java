// FILE: src/main/java/com/mobilewallet/service/impl/QRServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.qr.QRGenerateRequest;
import com.mobilewallet.dto.qr.QRGenerateResponse;
import com.mobilewallet.entity.QRTransaction;
import com.mobilewallet.entity.User;
import com.mobilewallet.exception.InvalidQRException;
import com.mobilewallet.exception.ResourceNotFoundException;
import com.mobilewallet.repository.QRTransactionRepository;
import com.mobilewallet.repository.UserRepository;
import com.mobilewallet.service.QRService;
import com.mobilewallet.util.QRCodeGenerator;
import com.mobilewallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QRServiceImpl implements QRService {

    private final QRTransactionRepository qrTransactionRepository;
    private final UserRepository userRepository;
    private final QRCodeGenerator qrCodeGenerator;
    private final ReferenceNumberGenerator referenceGenerator;

    @Value("${app.qr.expiration-seconds:300}")
    private int qrExpirationSeconds;

    @Override
    @Transactional
    public QRGenerateResponse generateQR(Long userId, QRGenerateRequest request) {
        log.info("Generating QR for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate QR token
        String qrToken = "QR" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        
        // Create QR payload (encrypted/encoded)
        String qrPayload = buildQRPayload(user, request, qrToken);

        // Save QR transaction
        QRTransaction qrTransaction = QRTransaction.builder()
                .user(user)
                .qrToken(qrToken)
                .qrPayload(qrPayload)
                .amount(request.getAmount())
                .status(QRTransaction.QRStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusSeconds(qrExpirationSeconds))
                .build();

        QRTransaction savedQR = qrTransactionRepository.save(qrTransaction);

        // Generate QR code image
        String qrImage = qrCodeGenerator.generateQRCodeImage(qrPayload, 300, 300);

        return QRGenerateResponse.builder()
                .qrToken(savedQR.getQrToken())
                .qrPayload(savedQR.getQrPayload())
                .qrImage(qrImage)
                .amount(savedQR.getAmount())
                .description(request.getDescription())
                .status(savedQR.getStatus().name())
                .expiresAt(savedQR.getExpiresAt())
                .userId(user.getId().toString())
                .userFullName(user.getFullName())
                .build();
    }

    @Override
    public QRTransaction validateQR(String qrPayload) {
        log.info("Validating QR payload");

        // Parse QR payload
        QRTransaction qrTransaction = qrTransactionRepository.findByQrPayload(qrPayload)
                .orElseThrow(() -> new InvalidQRException("Invalid QR code"));

        if (!qrTransaction.isActive()) {
            throw new InvalidQRException("QR code is not active. Status: " + qrTransaction.getStatus());
        }

        if (qrTransaction.isExpired()) {
            throw new InvalidQRException("QR code has expired");
        }

        return qrTransaction;
    }

    @Override
    @Transactional
    public void markQRUsed(String qrToken) {
        QRTransaction qrTransaction = qrTransactionRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new ResourceNotFoundException("QR transaction not found"));

        qrTransaction.setStatus(QRTransaction.QRStatus.USED);
        qrTransactionRepository.save(qrTransaction);
        log.info("QR marked as used: {}", qrToken);
    }

    @Override
    public boolean isQRValid(String qrToken) {
        return qrTransactionRepository.findValidActiveQR(qrToken, LocalDateTime.now())
                .isPresent();
    }

    @Override
    @Scheduled(fixedDelay = 300000) // Run every 5 minutes
    @Transactional
    public void cleanupExpiredQRCodes() {
        log.info("Cleaning up expired QR codes");
        LocalDateTime now = LocalDateTime.now();
        int expiredCount = qrTransactionRepository.expireExpiredQRCodes(now);
        if (expiredCount > 0) {
            log.info("Expired {} QR codes", expiredCount);
        }
    }

    private String buildQRPayload(User user, QRGenerateRequest request, String qrToken) {
        // Build a secure QR payload with required information
        // In production, this should be encrypted
        return String.format(
            "{\"token\":\"%s\",\"userId\":\"%s\",\"amount\":\"%s\",\"timestamp\":\"%s\",\"type\":\"%s\"}",
            qrToken,
            user.getId(),
            request.getAmount() != null ? request.getAmount().toString() : "0",
            LocalDateTime.now().toString(),
            request.getQrType()
        );
    }
}