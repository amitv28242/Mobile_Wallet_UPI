// FILE: src/main/java/com/mobilewallet/dto/payment/PaymentInitiateRequest.java
package com.mobilewallet.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiateRequest {

    @NotBlank(message = "Payee identifier is required")
    private String payeeIdentifier;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    private String description;
    private String paymentType = "QR_PAYMENT";
    private String idempotencyKey;
}