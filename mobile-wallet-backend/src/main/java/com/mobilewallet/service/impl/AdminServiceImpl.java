// FILE: src/main/java/com/mobilewallet/service/impl/AdminServiceImpl.java
package com.mobilewallet.service.impl;

import com.mobilewallet.dto.admin.AdminDashboardDTO;
import com.mobilewallet.dto.admin.AdminUserDTO;
import com.mobilewallet.dto.admin.ReportDTO;
import com.mobilewallet.entity.*;
import com.mobilewallet.exception.ResourceNotFoundException;
import com.mobilewallet.repository.*;
import com.mobilewallet.service.AdminService;
import com.mobilewallet.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final AuditUtil auditUtil;

    @Override
    public AdminDashboardDTO getDashboard() {
        log.info("Fetching admin dashboard statistics");

        long totalUsers = userRepository.count();
        long totalMerchants = merchantRepository.count();
        long totalConsumers = userRepository.countConsumers();
        long totalTransactions = transactionHistoryRepository.count();
        long totalPayments = paymentRepository.count();
        long totalRecharges = rechargeTransactionRepository.count();
        long totalBillPayments = billPaymentRepository.count();

        BigDecimal totalTransactionVolume = transactionHistoryRepository.findAll()
                .stream()
                .map(TransactionHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWalletBalance = walletRepository.getTotalActiveWalletBalance()
                .orElse(BigDecimal.ZERO);

        BigDecimal totalRevenue = paymentRepository.getTotalSuccessfulPayments()
                .orElse(BigDecimal.ZERO);

        // Get statistics by status
        Map<String, Long> transactionsByStatus = new HashMap<>();
        for (Payment.PaymentStatus status : Payment.PaymentStatus.values()) {
            long count = paymentRepository.countByStatus(status);
            transactionsByStatus.put(status.name(), count);
        }

        Map<String, Long> usersByStatus = new HashMap<>();
        usersByStatus.put("ENABLED", userRepository.findByEnabled(true).size());
        usersByStatus.put("DISABLED", userRepository.findByEnabled(false).size());
        usersByStatus.put("LOCKED", userRepository.findByLocked(true).size());

        return AdminDashboardDTO.builder()
                .totalUsers(totalUsers)
                .totalMerchants(totalMerchants)
                .totalConsumers(totalConsumers)
                .totalTransactions(totalTransactions)
                .totalPayments(totalPayments)
                .totalRecharges(totalRecharges)
                .totalBillPayments(totalBillPayments)
                .totalTransactionVolume(totalTransactionVolume)
                .totalWalletBalance(totalWalletBalance)
                .totalRevenue(totalRevenue)
                .transactionsByStatus(transactionsByStatus)
                .usersByStatus(usersByStatus)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Override
    public Page<AdminUserDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToAdminUserDTO);
    }

    @Override
    public Page<AdminUserDTO> getAllMerchants(Pageable pageable) {
        log.info("Fetching all merchants");
        Page<User> users = userRepository.findAllMerchants(pageable);
        return users.map(this::convertToAdminUserDTO);
    }

    @Override
    public Page<AdminUserDTO> getAllConsumers(Pageable pageable) {
        log.info("Fetching all consumers");
        Page<User> users = userRepository.findAllConsumers(pageable);
        return users.map(this::convertToAdminUserDTO);
    }

    @Override
    public AdminUserDTO getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return convertToAdminUserDTO(user);
    }

    @Override
    @Transactional
    public void blockUser(Long userId, String reason) {
        log.info("Blocking user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        
        user.setLocked(true);
        userRepository.save(user);

        auditUtil.logAdminAction("USER_BLOCKED", null, user, 
                "User blocked. Reason: " + reason);
    }

    @Override
    @Transactional
    public void unblockUser(Long userId) {
        log.info("Unblocking user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        
        user.setLocked(false);
        userRepository.save(user);

        auditUtil.logAdminAction("USER_UNBLOCKED", null, user, 
                "User unblocked");
    }

    @Override
    @Transactional
    public void blockMerchant(Long merchantId, String reason) {
        log.info("Blocking merchant: {}", merchantId);
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found: " + merchantId));
        
        User user = merchant.getUser();
        user.setLocked(true);
        userRepository.save(user);

        auditUtil.logAdminAction("MERCHANT_BLOCKED", null, user, 
                "Merchant blocked. Reason: " + reason);
    }

    @Override
    @Transactional
    public void unblockMerchant(Long merchantId) {
        log.info("Unblocking merchant: {}", merchantId);
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found: " + merchantId));
        
        User user = merchant.getUser();
        user.setLocked(false);
        userRepository.save(user);

        auditUtil.logAdminAction("MERCHANT_UNBLOCKED", null, user, 
                "Merchant unblocked");
    }

    @Override
    public Page<ReportDTO> getAllTransactions(String status, Pageable pageable) {
        log.info("Fetching all transactions with status: {}", status);
        
        if (status != null && !status.isEmpty()) {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
            Page<Payment> payments = paymentRepository.findByStatus(paymentStatus, pageable);
            return payments.map(this::convertToReportDTO);
        } else {
            Page<Payment> payments = paymentRepository.findAll(pageable);
            return payments.map(this::convertToReportDTO);
        }
    }

    @Override
    public Page<ReportDTO> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Fetching transactions by date range: {} to {}", startDate, endDate);
        Page<Payment> payments = paymentRepository.findByDateRange(startDate, endDate, pageable);
        return payments.map(this::convertToReportDTO);
    }

    @Override
    public ReportDTO getTransactionById(Long transactionId) {
        log.info("Fetching transaction by ID: {}", transactionId);
        TransactionHistory transaction = transactionHistoryRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
        return convertToReportDTO(transaction.getPayment());
    }

    @Override
    public Map<String, Object> generateReport(String startDate, String endDate, String reportType) {
        log.info("Generating report: type={}, start={}, end={}", reportType, startDate, endDate);
        
        Map<String, Object> report = new HashMap<>();
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();

        List<Payment> payments = paymentRepository.findByDateRange(start, end);
        
        report.put("totalTransactions", payments.size());
        report.put("totalAmount", payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        report.put("dateRange", Map.of("startDate", start, "endDate", end));
        report.put("reportType", reportType);
        
        // Group by status
        Map<String, Long> statusCount = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getStatus().name(),
                        Collectors.counting()
                ));
        report.put("statusCount", statusCount);
        
        // Group by payment type
        Map<String, Long> typeCount = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentType().name(),
                        Collectors.counting()
                ));
        report.put("typeCount", typeCount);
        
        return report;
    }

    @Override
    public Map<String, Object> getPaymentStatistics() {
        log.info("Fetching payment statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        
        long totalPayments = paymentRepository.count();
        long successfulPayments = paymentRepository.countByStatus(Payment.PaymentStatus.SUCCESS);
        long failedPayments = paymentRepository.countByStatus(Payment.PaymentStatus.FAILED);
        long pendingPayments = paymentRepository.countByStatus(Payment.PaymentStatus.PENDING);
        long cancelledPayments = paymentRepository.countByStatus(Payment.PaymentStatus.CANCELLED);
        long expiredPayments = paymentRepository.countByStatus(Payment.PaymentStatus.EXPIRED);

        statistics.put("totalPayments", totalPayments);
        statistics.put("successfulPayments", successfulPayments);
        statistics.put("failedPayments", failedPayments);
        statistics.put("pendingPayments", pendingPayments);
        statistics.put("cancelledPayments", cancelledPayments);
        statistics.put("expiredPayments", expiredPayments);
        
        statistics.put("successRate", totalPayments > 0 ? 
                (double) successfulPayments / totalPayments * 100 : 0);

        return statistics;
    }

    @Override
    public Page<Map<String, Object>> getAuditLogs(Pageable pageable) {
        log.info("Fetching audit logs");
        Page<AuditLog> auditLogs = auditLogRepository.findAllOrderByCreatedAtDesc(pageable);
        return auditLogs.map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("action", log.getAction());
            map.put("details", log.getDetails());
            map.put("userId", log.getUser() != null ? log.getUser().getId() : null);
            map.put("username", log.getUser() != null ? log.getUser().getUsername() : null);
            map.put("adminId", log.getAdmin() != null ? log.getAdmin().getId() : null);
            map.put("ipAddress", log.getIpAddress());
            map.put("createdAt", log.getCreatedAt());
            return map;
        });
    }

    @Override
    public Page<AdminUserDTO> searchUsers(String query, Pageable pageable) {
        log.info("Searching users with query: {}", query);
        // Simple implementation - can be enhanced with custom repository method
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getUsername().contains(query) || 
                            u.getEmail().contains(query) ||
                            u.getPhone().contains(query) ||
                            u.getFirstName().contains(query) ||
                            u.getLastName().contains(query))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
        
        long total = users.size();
        return new PageImpl<>(users.stream()
                .map(this::convertToAdminUserDTO)
                .collect(Collectors.toList()), pageable, total);
    }

    private AdminUserDTO convertToAdminUserDTO(User user) {
        Wallet wallet = walletRepository.findByUser_Id(user.getId()).orElse(null);
        Merchant merchant = merchantRepository.findByUser_Id(user.getId()).orElse(null);

        AdminUserDTO.WalletInfo walletInfo = null;
        if (wallet != null) {
            walletInfo = AdminUserDTO.WalletInfo.builder()
                    .id(wallet.getId())
                    .walletNumber(wallet.getWalletNumber())
                    .balance(wallet.getBalance().toString())
                    .currency(wallet.getCurrency())
                    .status(wallet.getStatus().name())
                    .build();
        }

        AdminUserDTO.MerchantInfo merchantInfo = null;
        if (merchant != null) {
            merchantInfo = AdminUserDTO.MerchantInfo.builder()
                    .id(merchant.getId())
                    .businessName(merchant.getBusinessName())
                    .businessType(merchant.getBusinessType())
                    .verified(merchant.getVerified())
                    .businessAddress(merchant.getBusinessAddress())
                    .businessPhone(merchant.getBusinessPhone())
                    .build();
        }

        return AdminUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .enabled(user.getEnabled())
                .locked(user.getLocked())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .wallet(walletInfo)
                .merchant(merchantInfo)
                .build();
    }

    private ReportDTO convertToReportDTO(Payment payment) {
        return ReportDTO.builder()
                .id(payment.getId())
                .referenceId(payment.getReferenceId())
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType().name())
                .status(payment.getStatus().name())
                .payerName(payment.getPayer().getFullName())
                .payeeName(payment.getPayee().getFullName())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}