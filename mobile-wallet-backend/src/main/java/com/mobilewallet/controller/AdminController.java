// FILE: src/main/java/com/mobilewallet/controller/AdminController.java
package com.mobilewallet.controller;

import com.mobilewallet.dto.admin.AdminDashboardDTO;
import com.mobilewallet.dto.admin.AdminUserDTO;
import com.mobilewallet.dto.admin.BlockUserRequest;
import com.mobilewallet.dto.admin.ReportDTO;
import com.mobilewallet.service.AdminService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get admin dashboard statistics")
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        AdminDashboardDTO dashboard = adminService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @Operation(summary = "Get all users")
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdminUserDTO> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get all merchants")
    @GetMapping("/merchants")
    public ResponseEntity<Page<AdminUserDTO>> getAllMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminUserDTO> merchants = adminService.getAllMerchants(pageable);
        return ResponseEntity.ok(merchants);
    }

    @Operation(summary = "Get all consumers")
    @GetMapping("/consumers")
    public ResponseEntity<Page<AdminUserDTO>> getAllConsumers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminUserDTO> consumers = adminService.getAllConsumers(pageable);
        return ResponseEntity.ok(consumers);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserDTO> getUserById(@PathVariable Long userId) {
        AdminUserDTO user = adminService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Block a user")
    @PutMapping("/users/{userId}/block")
    public ResponseEntity<Map<String, String>> blockUser(
            @PathVariable Long userId,
            @Valid @RequestBody BlockUserRequest request) {
        
        adminService.blockUser(userId, request.getReason());
        return ResponseEntity.ok(Map.of("message", "User blocked successfully"));
    }

    @Operation(summary = "Unblock a user")
    @PutMapping("/users/{userId}/unblock")
    public ResponseEntity<Map<String, String>> unblockUser(@PathVariable Long userId) {
        adminService.unblockUser(userId);
        return ResponseEntity.ok(Map.of("message", "User unblocked successfully"));
    }

    @Operation(summary = "Block a merchant")
    @PutMapping("/merchants/{merchantId}/block")
    public ResponseEntity<Map<String, String>> blockMerchant(
            @PathVariable Long merchantId,
            @Valid @RequestBody BlockUserRequest request) {
        
        adminService.blockMerchant(merchantId, request.getReason());
        return ResponseEntity.ok(Map.of("message", "Merchant blocked successfully"));
    }

    @Operation(summary = "Unblock a merchant")
    @PutMapping("/merchants/{merchantId}/unblock")
    public ResponseEntity<Map<String, String>> unblockMerchant(@PathVariable Long merchantId) {
        adminService.unblockMerchant(merchantId);
        return ResponseEntity.ok(Map.of("message", "Merchant unblocked successfully"));
    }

    @Operation(summary = "Get all transactions")
    @GetMapping("/transactions")
    public ResponseEntity<Page<ReportDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReportDTO> transactions = adminService.getAllTransactions(status, pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Get transactions by date range")
    @GetMapping("/transactions/range")
    public ResponseEntity<Page<ReportDTO>> getTransactionsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReportDTO> transactions = adminService.getTransactionsByDateRange(
                LocalDateTime.parse(startDate), LocalDateTime.parse(endDate), pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Get transaction by ID")
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ReportDTO> getTransactionById(@PathVariable Long transactionId) {
        ReportDTO transaction = adminService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @Operation(summary = "Generate payment reports")
    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> generateReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String reportType) {
        
        Map<String, Object> report = adminService.generateReport(startDate, endDate, reportType);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Get payment statistics")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
        Map<String, Object> statistics = adminService.getPaymentStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Get system audit logs")
    @GetMapping("/audit-logs")
    public ResponseEntity<Page<Map<String, Object>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Map<String, Object>> logs = adminService.getAuditLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Search users")
    @GetMapping("/search/users")
    public ResponseEntity<Page<AdminUserDTO>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserDTO> users = adminService.searchUsers(query, pageable);
        return ResponseEntity.ok(users);
    }
}