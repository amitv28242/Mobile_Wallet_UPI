// FILE: src/main/java/com/mobilewallet/controller/PaymentController.java
package com.mobilewallet.controller;

import com.mobilewallet.dto.payment.PaymentInitiateRequest;
import com.mobilewallet.dto.payment.PaymentResponse;
import com.mobilewallet.dto.payment.PaymentStatusResponse;
import com.mobilewallet.entity.User;
import com.mobilewallet.service.AuthService;
import com.mobilewallet.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "Payment processing APIs")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;

    @Operation(summary = "Initiate a payment")
    @PostMapping
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentInitiateRequest request) {
        User currentUser = authService.getCurrentUser();
        PaymentResponse response = paymentService.initiatePayment(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get payment details")
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get payment status by reference ID")
    @GetMapping("/status/{referenceId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable String referenceId) {
        PaymentStatusResponse response = paymentService.getPaymentStatus(referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user's payment history")
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Page<PaymentResponse>> getUserPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        User currentUser = authService.getCurrentUser();
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PaymentResponse> payments = paymentService.getUserPayments(currentUser.getId(), pageable);
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Get user's payment history by status")
    @GetMapping("/history/status/{status}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Page<PaymentResponse>> getUserPaymentsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        User currentUser = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<PaymentResponse> payments = paymentService.getUserPaymentsByStatus(
                currentUser.getId(), status, pageable);
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Cancel a payment")
    @PutMapping("/{paymentId}/cancel")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long paymentId) {
        paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok().build();
    }
}