package com.mobilewallet.dto.recharge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPaymentResponse {
    private Long id;
    private String referenceId;
    private String billType;
    private String billerCode;
    private String consumerNumber;
    private BigDecimal amount;
    private String status;
    private LocalDate dueDate;
    private String description;
    private LocalDateTime createdAt;
}
