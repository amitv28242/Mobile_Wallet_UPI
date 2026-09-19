package com.mobilewallet.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceResponse {
    private String walletNumber;
    private BigDecimal balance;
    private String currency;
    private String status;
    private Long userId;
    private String userFullName;
}
