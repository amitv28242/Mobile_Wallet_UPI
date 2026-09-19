package com.mobilewallet.dto.recharge;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequest {

    @NotBlank(message = "Operator is required")
    private String operator;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.00", message = "Amount must be at least 10.00")
    private BigDecimal amount;

    private String description;
}

