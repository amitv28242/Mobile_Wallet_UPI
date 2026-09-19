package com.mobilewallet.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPGenerateRequest {
    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Purpose is required")
    private String purpose;
}