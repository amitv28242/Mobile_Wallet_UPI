// FILE: src/main/java/com/mobilewallet/dto/qr/QRVerifyRequest.java
package com.mobilewallet.dto.qr;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRVerifyRequest {
    @NotBlank(message = "QR token is required")
    private String qrToken;
}