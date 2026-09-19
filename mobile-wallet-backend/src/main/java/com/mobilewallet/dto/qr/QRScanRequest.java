// FILE: src/main/java/com/mobilewallet/dto/qr/QRScanRequest.java
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
public class QRScanRequest {

    @NotBlank(message = "QR payload is required")
    private String qrPayload;

    private String idempotencyKey;
}