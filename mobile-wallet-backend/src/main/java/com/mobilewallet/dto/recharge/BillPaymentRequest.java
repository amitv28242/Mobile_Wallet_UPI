package com.mobilewallet.dto.recharge;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPaymentRequest {

    @NotBlank(message = "Bill type is required")
    private String billType;

    @NotBlank(message = "Biller code is required")
    private String billerCode;

    @NotBlank(message = "Consumer number is required")
    private String consumerNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    private BigDecimal amount;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private String description;
}