// FILE: src/main/java/com/mobilewallet/dto/payment/PaymentStatusResponse.java
package com.mobilewallet.dto.payment;

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
public class PaymentStatusResponse {
    private String referenceId;
    private String status;
    private BigDecimal amount;
    private String payerName;
    private String payeeName;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}