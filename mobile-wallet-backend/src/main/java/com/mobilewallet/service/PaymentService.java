// FILE: src/main/java/com/mobilewallet/service/PaymentService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.payment.PaymentInitiateRequest;
import com.mobilewallet.dto.payment.PaymentResponse;
import com.mobilewallet.dto.payment.PaymentStatusResponse;
import com.mobilewallet.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentResponse initiatePayment(Long userId, PaymentInitiateRequest request);

    PaymentResponse processQRPayment(Long userId, String qrPayload, String idempotencyKey);

    PaymentResponse getPayment(Long paymentId);

    PaymentStatusResponse getPaymentStatus(String referenceId);

    Page<PaymentResponse> getUserPayments(Long userId, Pageable pageable);

    Page<PaymentResponse> getUserPaymentsByStatus(Long userId, String status, Pageable pageable);

    Payment getPaymentEntity(Long paymentId);

    Payment getPaymentByReference(String referenceId);

    boolean isPaymentSuccessful(String referenceId);

    void cancelPayment(Long paymentId);

    void handleExpiredPayments();
}