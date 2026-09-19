// FILE: src/main/java/com/mobilewallet/dto/transaction/TransactionDTO.java
package com.mobilewallet.dto.transaction;

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
public class TransactionDTO {

    private Long id;
    private String reference;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime createdAt;

    private PaymentInfo payment;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private Long id;
        private String referenceId;
        private String paymentType;
        private PayerInfo payer;
        private PayeeInfo payee;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayerInfo {
        private Long id;
        private String fullName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayeeInfo {
        private Long id;
        private String fullName;
    }
}