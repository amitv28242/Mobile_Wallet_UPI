// FILE: src/main/java/com/mobilewallet/service/impl/WalletServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.wallet.AddMoneyRequest;
import com.mobilewallet.dto.wallet.AddMoneyResponse;
import com.mobilewallet.dto.wallet.WalletBalanceResponse;
import com.mobilewallet.dto.wallet.WalletResponse;
import com.mobilewallet.entity.User;
import com.mobilewallet.entity.Wallet;
import com.mobilewallet.exception.InsufficientBalanceException;
import com.mobilewallet.exception.ResourceNotFoundException;
import com.mobilewallet.exception.WalletException;
import com.mobilewallet.repository.UserRepository;
import com.mobilewallet.repository.WalletRepository;
import com.mobilewallet.service.WalletService;
import com.mobilewallet.util.AuditUtil;
import com.mobilewallet.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AuditUtil auditUtil;
    private final ReferenceNumberGenerator referenceGenerator;

    @Override
    @Transactional
    public Wallet createWallet(User user) {
        log.info("Creating wallet for user: {}", user.getId());

        String walletNumber = "WLT" + String.format("%08d", user.getId());

        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(java.math.BigDecimal.ZERO)
                .currency("INR")
                .walletNumber(walletNumber)
                .status(Wallet.WalletStatus.ACTIVE)
                .version(0)
                .build();

        Wallet saved = walletRepository.save(wallet);

        auditUtil.logAction("WALLET_CREATED", user,
                "Wallet created with number: " + walletNumber);

        return saved;
    }

    @Override
    public WalletResponse getWallet(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        
        return WalletResponse.builder()
                .id(wallet.getId())
                .walletNumber(wallet.getWalletNumber())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus().name())
                .userId(userId)
                .userFullName(wallet.getUser().getFullName())
                .build();
    }

    @Override
    public WalletBalanceResponse getBalance(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        
        return WalletBalanceResponse.builder()
                .walletNumber(wallet.getWalletNumber())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus().name())
                .userId(userId)
                .userFullName(wallet.getUser().getFullName())
                .build();
    }

    @Override
    @Transactional
    public AddMoneyResponse addMoney(Long userId, AddMoneyRequest request) {
        log.info("Adding money to wallet for user: {}", userId);

        Wallet wallet = getWalletWithLock(userId);
        validateWallet(wallet);

        BigDecimal amount = request.getAmount();
        String reference = referenceGenerator.generateReference("RECHARGE");

        // Credit the wallet
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.credit(amount);
        walletRepository.save(wallet);

        // TODO: Process payment via card/bank account
        // This would integrate with payment gateway

        auditUtil.logAction("WALLET_RECHARGE", wallet.getUser(), 
                "Wallet recharged with: " + amount + " " + wallet.getCurrency());

        return AddMoneyResponse.builder()
                .walletNumber(wallet.getWalletNumber())
                .amount(amount)
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .reference(reference)
                .status("SUCCESS")
                .message("Money added successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public void debitWallet(Long userId, BigDecimal amount, String reference) {
        log.info("Debiting wallet for user: {}, amount: {}", userId, amount);

        Wallet wallet = getWalletWithLock(userId);
        validateWallet(wallet);

        if (!wallet.hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException("Insufficient wallet balance. Available: " + 
                    wallet.getBalance() + ", Required: " + amount);
        }

        wallet.debit(amount);
        walletRepository.save(wallet);

        auditUtil.logAction("WALLET_DEBIT", wallet.getUser(), 
                "Wallet debited: " + amount + " " + wallet.getCurrency() + 
                " Reference: " + reference);
    }

    @Override
    @Transactional
    public void creditWallet(Long userId, BigDecimal amount, String reference) {
        log.info("Crediting wallet for user: {}, amount: {}", userId, amount);

        Wallet wallet = getWalletWithLock(userId);
        validateWallet(wallet);

        wallet.credit(amount);
        walletRepository.save(wallet);

        auditUtil.logAction("WALLET_CREDIT", wallet.getUser(), 
                "Wallet credited: " + amount + " " + wallet.getCurrency() + 
                " Reference: " + reference);
    }

    @Override
    public Wallet getWalletWithLock(Long userId) {
        return walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
    }

    @Override
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
    }

    @Override
    public Wallet getWalletByWalletNumber(String walletNumber) {
        return walletRepository.findByWalletNumber(walletNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletNumber));
    }

    @Override
    public boolean hasSufficientBalance(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        return wallet.hasSufficientBalance(amount);
    }

    @Override
    public void validateWallet(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        validateWallet(wallet);
    }

    private void validateWallet(Wallet wallet) {
        if (!wallet.isActive()) {
            throw new WalletException("Wallet is not active. Status: " + wallet.getStatus());
        }
    }
}