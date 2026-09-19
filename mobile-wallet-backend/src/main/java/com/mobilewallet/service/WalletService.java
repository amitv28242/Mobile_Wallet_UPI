// FILE: src/main/java/com/mobilewallet/service/WalletService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.wallet.AddMoneyRequest;
import com.mobilewallet.dto.wallet.AddMoneyResponse;
import com.mobilewallet.dto.wallet.WalletBalanceResponse;
import com.mobilewallet.dto.wallet.WalletResponse;
import com.mobilewallet.entity.User;
import com.mobilewallet.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    Wallet createWallet(User user);

    WalletResponse getWallet(Long userId);

    WalletBalanceResponse getBalance(Long userId);

    AddMoneyResponse addMoney(Long userId, AddMoneyRequest request);

    void debitWallet(Long userId, BigDecimal amount, String reference);

    void creditWallet(Long userId, BigDecimal amount, String reference);

    Wallet getWalletWithLock(Long userId);

    Wallet getWalletByUserId(Long userId);

    Wallet getWalletByWalletNumber(String walletNumber);

    boolean hasSufficientBalance(Long userId, BigDecimal amount);

    void validateWallet(Long userId);
}