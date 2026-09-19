// FILE: src/main/java/com/mobilewallet/service/AdminService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.admin.AdminDashboardDTO;
import com.mobilewallet.dto.admin.AdminUserDTO;
import com.mobilewallet.dto.admin.ReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

public interface AdminService {

    AdminDashboardDTO getDashboard();

    Page<AdminUserDTO> getAllUsers(Pageable pageable);

    Page<AdminUserDTO> getAllMerchants(Pageable pageable);

    Page<AdminUserDTO> getAllConsumers(Pageable pageable);

    AdminUserDTO getUserById(Long userId);

    void blockUser(Long userId, String reason);

    void unblockUser(Long userId);

    void blockMerchant(Long merchantId, String reason);

    void unblockMerchant(Long merchantId);

    Page<ReportDTO> getAllTransactions(String status, Pageable pageable);

    Page<ReportDTO> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    ReportDTO getTransactionById(Long transactionId);

    Map<String, Object> generateReport(String startDate, String endDate, String reportType);

    Map<String, Object> getPaymentStatistics();

    Page<Map<String, Object>> getAuditLogs(Pageable pageable);

    Page<AdminUserDTO> searchUsers(String query, Pageable pageable);
}