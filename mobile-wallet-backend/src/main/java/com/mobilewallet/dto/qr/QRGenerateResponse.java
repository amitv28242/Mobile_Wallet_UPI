// FILE: src/main/java/com/mobilewallet/dto/qr/QRGenerateResponse.java
package com.mobilewallet.dto.qr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRGenerateResponse {

    private String qrToken;
    private String qrPayload;
    private String qrImage; // Base64 encoded QR code image
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime expiresAt;
    private String userId;
    private String userFullName;
}