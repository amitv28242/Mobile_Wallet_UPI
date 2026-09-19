// FILE: src/main/java/com/mobilewallet/controller/QRController.java
package com.mobilewallet.controller;

import com.mobilewallet.dto.payment.PaymentResponse;
import com.mobilewallet.dto.qr.QRGenerateRequest;
import com.mobilewallet.dto.qr.QRGenerateResponse;
import com.mobilewallet.dto.qr.QRScanRequest;
import com.mobilewallet.entity.User;
import com.mobilewallet.service.AuthService;
import com.mobilewallet.service.PaymentService;
import com.mobilewallet.service.QRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "QR", description = "QR code generation and scanning APIs")
@SecurityRequirement(name = "bearerAuth")
public class QRController {

    private final QRService qrService;
    private final PaymentService paymentService;
    private final AuthService authService;

    @Operation(summary = "Generate QR code for payment")
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<QRGenerateResponse> generateQR(@Valid @RequestBody QRGenerateRequest request) {
        User currentUser = authService.getCurrentUser();
        QRGenerateResponse response = qrService.generateQR(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Scan QR code and process payment")
    @PostMapping("/scan")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<PaymentResponse> scanQR(@Valid @RequestBody QRScanRequest request) {
        User currentUser = authService.getCurrentUser();
        PaymentResponse response = paymentService.processQRPayment(
                currentUser.getId(), request.getQrPayload(), request.getIdempotencyKey());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate QR code")
    @PostMapping("/validate")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Boolean> validateQR(@RequestBody String qrPayload) {
        boolean isValid = qrService.validateQR(qrPayload) != null;
        return ResponseEntity.ok(isValid);
    }
}