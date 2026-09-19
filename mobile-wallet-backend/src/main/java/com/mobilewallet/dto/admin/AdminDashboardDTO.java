// FILE: src/main/java/com/mobilewallet/dto/admin/AdminDashboardDTO.java
package com.mobilewallet.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private Long totalUsers;
    private Long totalMerchants;
    private Long totalConsumers;
    private Long totalTransactions;
    private Long totalPayments;
    private Long totalRecharges;
    private Long totalBillPayments;
    private BigDecimal totalTransactionVolume;
    private BigDecimal totalWalletBalance;
    private BigDecimal totalRevenue;
    private Map<String, Long> transactionsByStatus;
    private Map<String, Long> usersByStatus;
    private Map<String, BigDecimal> paymentsByType;
    private LocalDateTime lastUpdated;
    private DailyStats dailyStats;
    private WeeklyStats weeklyStats;
    private MonthlyStats monthlyStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private Long transactions;
        private BigDecimal volume;
        private Long newUsers;
        private Long activeUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyStats {
        private Long transactions;
        private BigDecimal volume;
        private Long newUsers;
        private Long activeUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyStats {
        private Long transactions;
        private BigDecimal volume;
        private Long newUsers;
        private Long activeUsers;
    }
}

