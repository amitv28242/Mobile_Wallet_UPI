package com.mobilewallet.dto.recharge;

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
public class RechargeResponse {
    private Long id;
    private String referenceId;
    private String operator;
    private String phoneNumber;
    private BigDecimal amount;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
