// FILE: src/main/java/com/mobilewallet/dto/payment/PaymentResponse.java
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
public class PaymentResponse {

    private Long id;
    private String referenceId;
    private BigDecimal amount;
    private String paymentType;
    private String status;
    private PayerInfo payer;
    private PayeeInfo payee;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayerInfo {
        private Long id;
        private String fullName;
        private String walletNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayeeInfo {
        private Long id;
        private String fullName;
        private String walletNumber;
    }
}