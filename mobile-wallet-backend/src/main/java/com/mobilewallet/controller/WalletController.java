// FILE: src/main/java/com/mobilewallet/controller/WalletController.java
package com.mobilewallet.controller;

import com.mobilewallet.dto.wallet.AddMoneyRequest;
import com.mobilewallet.dto.wallet.AddMoneyResponse;
import com.mobilewallet.dto.wallet.WalletBalanceResponse;
import com.mobilewallet.dto.wallet.WalletResponse;
import com.mobilewallet.entity.User;
import com.mobilewallet.service.AuthService;
import com.mobilewallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallet", description = "Wallet management APIs")
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final WalletService walletService;
    private final AuthService authService;

    @Operation(summary = "Get wallet details")
    @GetMapping
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<WalletResponse> getWallet() {
        User currentUser = authService.getCurrentUser();
        WalletResponse response = walletService.getWallet(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get wallet balance")
    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<WalletBalanceResponse> getBalance() {
        User currentUser = authService.getCurrentUser();
        WalletBalanceResponse response = walletService.getBalance(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add money to wallet")
    @PostMapping("/add-money")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<AddMoneyResponse> addMoney(@Valid @RequestBody AddMoneyRequest request) {
        User currentUser = authService.getCurrentUser();
        AddMoneyResponse response = walletService.addMoney(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get wallet by user ID (Admin only)")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletResponse> getWalletByUserId(@PathVariable Long userId) {
        WalletResponse response = walletService.getWallet(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get wallet balance by user ID (Admin only)")
    @GetMapping("/user/{userId}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletBalanceResponse> getBalanceByUserId(@PathVariable Long userId) {
        WalletBalanceResponse response = walletService.getBalance(userId);
        return ResponseEntity.ok(response);
    }
}