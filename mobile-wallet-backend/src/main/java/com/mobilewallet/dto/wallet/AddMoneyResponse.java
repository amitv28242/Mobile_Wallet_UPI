// FILE: src/main/java/com/mobilewallet/dto/wallet/AddMoneyResponse.java
package com.mobilewallet.dto.wallet;

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
public class AddMoneyResponse {
    private String walletNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency;
    private String reference;
    private String status;
    private String message;
    private LocalDateTime timestamp;
}

