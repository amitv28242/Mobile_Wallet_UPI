// FILE: src/main/java/com/mobilewallet/controller/BankAccountController.java
package com.mobilewallet.controller;

import com.mobilewallet.dto.bank.BankAccountRequest;
import com.mobilewallet.dto.bank.BankAccountResponse;
import com.mobilewallet.dto.bank.BankTransferRequest;
import com.mobilewallet.entity.User;
import com.mobilewallet.service.AuthService;
import com.mobilewallet.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bank-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bank Accounts", description = "Bank account management APIs")
@SecurityRequirement(name = "bearerAuth")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final AuthService authService;

    @Operation(summary = "Add a new bank account")
    @PostMapping
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<BankAccountResponse> addBankAccount(@Valid @RequestBody BankAccountRequest request) {
        User currentUser = authService.getCurrentUser();
        BankAccountResponse response = bankAccountService.addBankAccount(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all bank accounts")
    @GetMapping
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<List<BankAccountResponse>> getBankAccounts() {
        User currentUser = authService.getCurrentUser();
        List<BankAccountResponse> accounts = bankAccountService.getBankAccounts(currentUser.getId());
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Get bank account by ID")
    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<BankAccountResponse> getBankAccount(@PathVariable Long accountId) {
        User currentUser = authService.getCurrentUser();
        BankAccountResponse response = bankAccountService.getBankAccount(currentUser.getId(), accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update bank account")
    @PutMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<BankAccountResponse> updateBankAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody BankAccountRequest request) {
        
        User currentUser = authService.getCurrentUser();
        BankAccountResponse response = bankAccountService.updateBankAccount(currentUser.getId(), accountId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete bank account")
    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Map<String, String>> deleteBankAccount(@PathVariable Long accountId) {
        User currentUser = authService.getCurrentUser();
        bankAccountService.deleteBankAccount(currentUser.getId(), accountId);
        return ResponseEntity.ok(Map.of("message", "Bank account deleted successfully"));
    }

    @Operation(summary = "Set default bank account")
    @PutMapping("/{accountId}/default")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Map<String, String>> setDefaultBankAccount(@PathVariable Long accountId) {
        User currentUser = authService.getCurrentUser();
        bankAccountService.setDefaultBankAccount(currentUser.getId(), accountId);
        return ResponseEntity.ok(Map.of("message", "Default bank account set successfully"));
    }

    @Operation(summary = "Transfer wallet balance to bank")
    @PostMapping("/{accountId}/transfer")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Map<String, String>> transferToBank(
            @PathVariable Long accountId,
            @Valid @RequestBody BankTransferRequest request) {
        
        User currentUser = authService.getCurrentUser();
        bankAccountService.transferToBank(currentUser.getId(), accountId, request);
        return ResponseEntity.ok(Map.of("message", "Transfer initiated successfully"));
    }
}