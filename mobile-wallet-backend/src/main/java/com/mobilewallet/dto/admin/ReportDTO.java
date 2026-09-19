package com.mobilewallet.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private String referenceId;
    private BigDecimal amount;
    private String paymentType;
    private String status;
    private String payerName;
    private String payeeName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
