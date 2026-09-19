// FILE: src/main/java/com/mobilewallet/service/impl/PaymentServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.payment.PaymentInitiateRequest;
import com.mobilewallet.dto.payment.PaymentResponse;
import com.mobilewallet.dto.payment.PaymentStatusResponse;
import com.mobilewallet.entity.Payment;
import com.mobilewallet.entity.QRTransaction;
import com.mobilewallet.entity.TransactionHistory;
import com.mobilewallet.entity.User;
import com.mobilewallet.entity.Wallet;
import com.mobilewallet.exception.*;
import com.mobilewallet.repository.PaymentRepository;
import com.mobilewallet.repository.QRTransactionRepository;
import com.mobilewallet.repository.TransactionHistoryRepository;
import com.mobilewallet.repository.UserRepository;
import com.mobilewallet.service.PaymentService;
import com.mobilewallet.service.QRService;
import com.mobilewallet.service.WalletService;
import com.mobilewallet.util.AuditUtil;
import com.mobilewallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final QRTransactionRepository qrTransactionRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final QRService qrService;
    private final AuditUtil auditUtil;
    private final ReferenceNumberGenerator referenceGenerator;

    @Override
    @Transactional
    public PaymentResponse initiatePayment(Long userId, PaymentInitiateRequest request) {
        log.info("Initiating payment from user: {}", userId);

        // Validate idempotency
        if (request.getIdempotencyKey() != null) {
            Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existingPayment.isPresent()) {
                return convertToResponse(existingPayment.get());
            }
        }

        User payer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payer not found"));

        User payee = userRepository.findByIdentifier(request.getPayeeIdentifier())
                .orElseThrow(() -> new ResourceNotFoundException("Payee not found"));

        if (payer.getId().equals(payee.getId())) {
            throw new InvalidPaymentException("Cannot pay yourself");
        }

        Wallet payerWallet = walletService.getWalletWithLock(userId);
        if (!payerWallet.hasSufficientBalance(request.getAmount())) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // Generate reference
        String referenceId = referenceGenerator.generateReference("PAY");

        // Create payment
        Payment payment = Payment.builder()
                .payer(payer)
                .payee(payee)
                .wallet(payerWallet)
                .amount(request.getAmount())
                .paymentType(Payment.PaymentType.valueOf(request.getPaymentType()))
                .status(Payment.PaymentStatus.INITIATED)
                .referenceId(referenceId)
                .description(request.getDescription())
                .idempotencyKey(request.getIdempotencyKey())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Process payment
        processPayment(savedPayment);

        auditUtil.logAction("PAYMENT_INITIATED", payer, 
                "Payment initiated to: " + payee.getUsername() + 
                " Amount: " + request.getAmount());

        return convertToResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse processQRPayment(Long userId, String qrPayload, String idempotencyKey) {
        log.info("Processing QR payment from user: {}", userId);

        // Validate idempotency
        if (idempotencyKey != null) {
            Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);
            if (existingPayment.isPresent()) {
                return convertToResponse(existingPayment.get());
            }
        }

        // Validate QR
        QRTransaction qrTransaction = qrService.validateQR(qrPayload);

        // Check if QR is already used
        if (qrTransaction.getStatus() == QRTransaction.QRStatus.USED) {
            throw new InvalidQRException("QR code has already been used");
        }

        // Check if QR is expired
        if (qrTransaction.isExpired()) {
            throw new InvalidQRException("QR code has expired");
        }

        User payer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payer not found"));

        User payee = qrTransaction.getUser();

        if (payer.getId().equals(payee.getId())) {
            throw new InvalidPaymentException("Cannot pay yourself");
        }

        BigDecimal amount = qrTransaction.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Invalid QR amount");
        }

        Wallet payerWallet = walletService.getWalletWithLock(userId);
        if (!payerWallet.hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // Generate reference
        String referenceId = referenceGenerator.generateReference("QRPAY");

        // Create payment
        Payment payment = Payment.builder()
                .payer(payer)
                .payee(payee)
                .wallet(payerWallet)
                .amount(amount)
                .paymentType(Payment.PaymentType.QR_PAYMENT)
                .status(Payment.PaymentStatus.INITIATED)
                .referenceId(referenceId)
                .description("QR Payment to " + payee.getFullName())
                .idempotencyKey(idempotencyKey)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Process payment
        processPayment(savedPayment);

        // Mark QR as used
        qrTransaction.setStatus(QRTransaction.QRStatus.USED);
        qrTransactionRepository.save(qrTransaction);

        auditUtil.logAction("QR_PAYMENT", payer, 
                "QR Payment to: " + payee.getUsername() + 
                " Amount: " + amount + 
                " QR Token: " + qrTransaction.getQrToken());

        return convertToResponse(savedPayment);
    }

    @Transactional
    protected void processPayment(Payment payment) {
        log.info("Processing payment: {}", payment.getReferenceId());

        try {
            payment.setStatus(Payment.PaymentStatus.PENDING);
            paymentRepository.save(payment);

            User payer = payment.getPayer();
            User payee = payment.getPayee();
            BigDecimal amount = payment.getAmount();

            // Debit payer
            walletService.debitWallet(payer.getId(), amount, payment.getReferenceId());

            // Credit payee
            walletService.creditWallet(payee.getId(), amount, payment.getReferenceId());

            // Create transaction history entries
            createTransactionHistory(payment, payer, amount, TransactionHistory.TransactionType.DEBIT, 
                    "Payment to " + payee.getFullName());
            
            createTransactionHistory(payment, payee, amount, TransactionHistory.TransactionType.CREDIT, 
                    "Payment from " + payer.getFullName());

            // Update payment status
            payment.markSuccess();
            paymentRepository.save(payment);

            // TODO: Send notifications
            // notificationService.sendPaymentNotification(payment);

            log.info("Payment processed successfully: {}", payment.getReferenceId());

        } catch (Exception e) {
            log.error("Payment processing failed: {}", payment.getReferenceId(), e);
            payment.markFailed();
            paymentRepository.save(payment);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage());
        }
    }

    private void createTransactionHistory(Payment payment, User user, BigDecimal amount, 
                                         TransactionHistory.TransactionType type, String description) {
        Wallet wallet = walletService.getWalletByUserId(user.getId());
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = type == TransactionHistory.TransactionType.CREDIT ?
                balanceBefore.add(amount) : balanceBefore.subtract(amount);

        TransactionHistory history = TransactionHistory.builder()
                .payment(payment)
                .wallet(wallet)
                .user(user)
                .amount(amount)
                .transactionType(type)
                .status(TransactionHistory.TransactionStatus.SUCCESS)
                .reference(payment.getReferenceId())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(description)
                .build();

        transactionHistoryRepository.save(history);
    }

    @Override
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = getPaymentEntity(paymentId);
        return convertToResponse(payment);
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(String referenceId) {
        Payment payment = getPaymentByReference(referenceId);
        return PaymentStatusResponse.builder()
                .referenceId(payment.getReferenceId())
                .status(payment.getStatus().name())
                .amount(payment.getAmount())
                .payerName(payment.getPayer().getFullName())
                .payeeName(payment.getPayee().getFullName())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }

    @Override
    public Page<PaymentResponse> getUserPayments(Long userId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
        return payments.map(this::convertToResponse);
    }

    @Override
    public Page<PaymentResponse> getUserPaymentsByStatus(Long userId, String status, Pageable pageable) {
        Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
        Page<Payment> payments = paymentRepository.findByUserIdAndStatus(userId, paymentStatus, pageable);
        return payments.map(this::convertToResponse);
    }

    @Override
    public Payment getPaymentEntity(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
    }

    @Override
    public Payment getPaymentByReference(String referenceId) {
        return paymentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + referenceId));
    }

    @Override
    public boolean isPaymentSuccessful(String referenceId) {
        Payment payment = getPaymentByReference(referenceId);
        return payment.isSuccess();
    }

    @Override
    @Transactional
    public void cancelPayment(Long paymentId) {
        Payment payment = getPaymentEntity(paymentId);
        
        if (!payment.isPending() && payment.getStatus() != Payment.PaymentStatus.INITIATED) {
            throw new InvalidPaymentException("Cannot cancel payment in status: " + payment.getStatus());
        }

        payment.markCancelled();
        paymentRepository.save(payment);

        auditUtil.logAction("PAYMENT_CANCELLED", payment.getPayer(), 
                "Payment cancelled: " + payment.getReferenceId());
    }

    @Override
    @Scheduled(fixedDelay = 60000) // Run every minute
    @Transactional
    public void handleExpiredPayments() {
        log.info("Handling expired payments");
        LocalDateTime now = LocalDateTime.now();
        var expiredPayments = paymentRepository.findExpiredPendingPayments(now);
        
        for (Payment payment : expiredPayments) {
            if (payment.isPending() || payment.getStatus() == Payment.PaymentStatus.INITIATED) {
                payment.markExpired();
                paymentRepository.save(payment);
                log.info("Expired payment: {}", payment.getReferenceId());
            }
        }
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .referenceId(payment.getReferenceId())
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType().name())
                .status(payment.getStatus().name())
                .payer(PaymentResponse.PayerInfo.builder()
                        .id(payment.getPayer().getId())
                        .fullName(payment.getPayer().getFullName())
                        .walletNumber(walletService.getWalletByUserId(payment.getPayer().getId()).getWalletNumber())
                        .build())
                .payee(PaymentResponse.PayeeInfo.builder()
                        .id(payment.getPayee().getId())
                        .fullName(payment.getPayee().getFullName())
                        .walletNumber(walletService.getWalletByUserId(payment.getPayee().getId()).getWalletNumber())
                        .build())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}