// FILE: src/main/java/com/mobilewallet/dto/qr/QRGenerateRequest.java
package com.mobilewallet.dto.qr;

import jakarta.validation.constraints.DecimalMin;
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
public class QRGenerateRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", message = "Amount must be at least 0.00")
    private BigDecimal amount;

    private String description;
    private String qrType = "PAYMENT";
}