// FILE: src/main/java/com/mobilewallet/service/impl/BankAccountServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.bank.BankAccountRequest;
import com.mobilewallet.dto.bank.BankAccountResponse;
import com.mobilewallet.dto.bank.BankTransferRequest;
import com.mobilewallet.entity.BankAccount;
import com.mobilewallet.entity.User;
import com.mobilewallet.exception.ResourceNotFoundException;
import com.mobilewallet.exception.WalletException;
import com.mobilewallet.repository.BankAccountRepository;
import com.mobilewallet.repository.UserRepository;
import com.mobilewallet.service.BankAccountService;
import com.mobilewallet.service.WalletService;
import com.mobilewallet.util.AESEncryptionUtil;
import com.mobilewallet.util.AuditUtil;
import com.mobilewallet.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final AESEncryptionUtil encryptionUtil;
    private final AuditUtil auditUtil;

    @Override
    @Transactional
    public BankAccountResponse addBankAccount(Long userId, BankAccountRequest request) {
        log.info("Adding bank account for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate IFSC code
        if (!ValidationUtil.isValidIFSC(request.getIfscCode())) {
            throw new IllegalArgumentException("Invalid IFSC code format");
        }

        // Encrypt sensitive data
        String encryptedData = encryptionUtil.encrypt(
                request.getAccountNumber() + "|" + request.getIfscCode()
        );

        String maskedAccountNumber = ValidationUtil.maskAccountNumber(request.getAccountNumber());

        BankAccount account = BankAccount.builder()
                .user(user)
                .accountNumberMasked(maskedAccountNumber)
                .ifscCode(request.getIfscCode())
                .bankName(request.getBankName())
                .accountHolderName(request.getAccountHolderName())
                .encryptedData(encryptedData)
                .isDefault(request.getIsDefault() != null && request.getIsDefault())
                .build();

        // If this is the first account, make it default
        if (bankAccountRepository.countByUserId(userId) == 0) {
            account.setIsDefault(true);
        } else if (account.getIsDefault()) {
            // Clear existing default if setting this as default
            bankAccountRepository.clearDefaultAccounts(userId);
        }

        BankAccount savedAccount = bankAccountRepository.save(account);

        auditUtil.logAction("BANK_ACCOUNT_ADDED", user, 
                "Bank account added: " + maskedAccountNumber);

        return convertToResponse(savedAccount);
    }

    @Override
    public List<BankAccountResponse> getBankAccounts(Long userId) {
        log.info("Fetching bank accounts for user: {}", userId);
        
        List<BankAccount> accounts = bankAccountRepository.findByUser_Id(userId);
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountResponse getBankAccount(Long userId, Long accountId) {
        log.info("Fetching bank account: {} for user: {}", accountId, userId);
        
        BankAccount account = bankAccountRepository.findByIdAndUser_Id(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));
        
        return convertToResponse(account);
    }

    @Override
    @Transactional
    public BankAccountResponse updateBankAccount(Long userId, Long accountId, BankAccountRequest request) {
        log.info("Updating bank account: {} for user: {}", accountId, userId);

        BankAccount account = bankAccountRepository.findByIdAndUser_Id(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));

        // Update fields
        if (request.getAccountNumber() != null) {
            String encryptedData = encryptionUtil.encrypt(
                    request.getAccountNumber() + "|" + (request.getIfscCode() != null ? 
                            request.getIfscCode() : account.getIfscCode())
            );
            account.setEncryptedData(encryptedData);
            account.setAccountNumberMasked(ValidationUtil.maskAccountNumber(request.getAccountNumber()));
        }

        if (request.getIfscCode() != null) {
            if (!ValidationUtil.isValidIFSC(request.getIfscCode())) {
                throw new IllegalArgumentException("Invalid IFSC code format");
            }
            account.setIfscCode(request.getIfscCode());
        }

        if (request.getBankName() != null) {
            account.setBankName(request.getBankName());
        }

        if (request.getAccountHolderName() != null) {
            account.setAccountHolderName(request.getAccountHolderName());
        }

        if (request.getIsDefault() != null && request.getIsDefault() && !account.getIsDefault()) {
            bankAccountRepository.clearDefaultAccounts(userId);
            account.setIsDefault(true);
        }

        BankAccount updatedAccount = bankAccountRepository.save(account);

        auditUtil.logAction("BANK_ACCOUNT_UPDATED", account.getUser(),
                "Bank account updated: " + account.getAccountNumberMasked());

        return convertToResponse(updatedAccount);
    }

    @Override
    @Transactional
    public void deleteBankAccount(Long userId, Long accountId) {
        log.info("Deleting bank account: {} for user: {}", accountId, userId);

        BankAccount account = bankAccountRepository.findByIdAndUser_Id(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));

        // If this is the default account, clear default
        if (account.getIsDefault()) {
            bankAccountRepository.clearDefaultAccounts(userId);
        }

        bankAccountRepository.delete(account);

        auditUtil.logAction("BANK_ACCOUNT_DELETED", account.getUser(),
                "Bank account deleted: " + account.getAccountNumberMasked());
    }

    @Override
    @Transactional
    public void setDefaultBankAccount(Long userId, Long accountId) {
        log.info("Setting default bank account: {} for user: {}", accountId, userId);

        BankAccount account = bankAccountRepository.findByIdAndUser_Id(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));

        bankAccountRepository.clearDefaultAccounts(userId);
        account.setIsDefault(true);
        bankAccountRepository.save(account);

        auditUtil.logAction("BANK_ACCOUNT_DEFAULT_SET", account.getUser(),
                "Default bank account set: " + account.getAccountNumberMasked());
    }

    @Override
    @Transactional
    public void transferToBank(Long userId, Long accountId, BankTransferRequest request) {
        log.info("Transferring to bank: {} for user: {}, amount: {}", accountId, userId, request.getAmount());

        BankAccount account = bankAccountRepository.findByIdAndUser_Id(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));

        // Validate wallet balance
        if (!walletService.hasSufficientBalance(userId, request.getAmount())) {
            throw new WalletException("Insufficient wallet balance");
        }

        // Debit from wallet
        walletService.debitWallet(userId, request.getAmount(), "BANK_TRANSFER_" + accountId);

        // TODO: Integrate with bank API for actual transfer
        // This would call a banking API to transfer funds

        auditUtil.logAction("BANK_TRANSFER", account.getUser(),
                "Bank transfer initiated: " + request.getAmount() + 
                " to account: " + account.getAccountNumberMasked());
    }

    private BankAccountResponse convertToResponse(BankAccount account) {
        return BankAccountResponse.builder()
                .id(account.getId())
                .accountNumberMasked(account.getAccountNumberMasked())
                .ifscCode(account.getIfscCode())
                .bankName(account.getBankName())
                .accountHolderName(account.getAccountHolderName())
                .isDefault(account.getIsDefault())
                .createdAt(account.getCreatedAt().toString())
                .build();
    }
}