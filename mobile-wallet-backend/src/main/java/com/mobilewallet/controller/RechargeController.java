// FILE: src/main/java/com/mobilewallet/controller/RechargeController.java
package com.mobilewallet.controller;

import com.mobilewallet.dto.recharge.BillPaymentRequest;
import com.mobilewallet.dto.recharge.BillPaymentResponse;
import com.mobilewallet.dto.recharge.RechargeRequest;
import com.mobilewallet.dto.recharge.RechargeResponse;
import com.mobilewallet.entity.User;
import com.mobilewallet.service.AuthService;
import com.mobilewallet.service.RechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recharge")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recharge", description = "Recharge and bill payment APIs")
@SecurityRequirement(name = "bearerAuth")
public class RechargeController {

    private final RechargeService rechargeService;
    private final AuthService authService;

    @Operation(summary = "Mobile recharge")
    @PostMapping("/mobile")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<RechargeResponse> mobileRecharge(@Valid @RequestBody RechargeRequest request) {
        User currentUser = authService.getCurrentUser();
        RechargeResponse response = rechargeService.mobileRecharge(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "DTH recharge")
    @PostMapping("/dth")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<RechargeResponse> dthRecharge(@Valid @RequestBody RechargeRequest request) {
        User currentUser = authService.getCurrentUser();
        RechargeResponse response = rechargeService.dthRecharge(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Pay bill")
    @PostMapping("/bill")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<BillPaymentResponse> payBill(@Valid @RequestBody BillPaymentRequest request) {
        User currentUser = authService.getCurrentUser();
        BillPaymentResponse response = rechargeService.payBill(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get recharge history")
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Page<RechargeResponse>> getRechargeHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        User currentUser = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RechargeResponse> history = rechargeService.getRechargeHistory(currentUser.getId(), pageable);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Get bill payment history")
    @GetMapping("/bills/history")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Page<BillPaymentResponse>> getBillPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        User currentUser = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BillPaymentResponse> history = rechargeService.getBillPaymentHistory(currentUser.getId(), pageable);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Get recharge by reference ID")
    @GetMapping("/{referenceId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<RechargeResponse> getRechargeByReference(@PathVariable String referenceId) {
        User currentUser = authService.getCurrentUser();
        RechargeResponse response = rechargeService.getRechargeByReference(currentUser.getId(), referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get bill by reference ID")
    @GetMapping("/bill/{referenceId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<BillPaymentResponse> getBillByReference(@PathVariable String referenceId) {
        User currentUser = authService.getCurrentUser();
        BillPaymentResponse response = rechargeService.getBillByReference(currentUser.getId(), referenceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get available operators")
    @GetMapping("/operators")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Map<String, List<String>>> getAvailableOperators() {
        Map<String, List<String>> operators = rechargeService.getAvailableOperators();
        return ResponseEntity.ok(operators);
    }

    @Operation(summary = "Get bill types")
    @GetMapping("/bill-types")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<List<String>> getBillTypes() {
        List<String> billTypes = rechargeService.getBillTypes();
        return ResponseEntity.ok(billTypes);
    }
}