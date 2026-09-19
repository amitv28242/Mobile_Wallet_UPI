// FILE: src/main/java/com/mobilewallet/service/BankAccountService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.bank.BankAccountRequest;
import com.mobilewallet.dto.bank.BankAccountResponse;
import com.mobilewallet.dto.bank.BankTransferRequest;

import java.util.List;

public interface BankAccountService {

    BankAccountResponse addBankAccount(Long userId, BankAccountRequest request);

    List<BankAccountResponse> getBankAccounts(Long userId);

    BankAccountResponse getBankAccount(Long userId, Long accountId);

    BankAccountResponse updateBankAccount(Long userId, Long accountId, BankAccountRequest request);

    void deleteBankAccount(Long userId, Long accountId);

    void setDefaultBankAccount(Long userId, Long accountId);

    void transferToBank(Long userId, Long accountId, BankTransferRequest request);
}