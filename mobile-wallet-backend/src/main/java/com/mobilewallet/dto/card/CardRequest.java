package com.mobilewallet.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{15,16}$", message = "Invalid card number format")
    private String cardNumber;

    @NotBlank(message = "Cardholder name is required")
    private String cardholderName;

    @NotBlank(message = "Expiry month is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Invalid expiry month format")
    private String expiryMonth;
    
    @NotBlank(message = "Expiry year is required")
    @Pattern(regexp = "^\\d{4}$", message = "Invalid expiry year format")
    private String expiryYear;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^\\d{3,4}$", message = "Invalid CVV format")
    @Size(min = 3, max = 4, message = "CVV must be 3 or 4 digits")
    private String cvv;

    private Boolean isDefault;
}