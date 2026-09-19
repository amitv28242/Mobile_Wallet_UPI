// FILE: src/main/java/com/mobilewallet/dto/auth/OTPRequest.java
package com.mobilewallet.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Purpose is required")
    private String purpose;

    @NotBlank(message = "OTP code is required")
    @Size(min = 4, max = 10, message = "OTP must be between 4 and 10 characters")
    private String otpCode;
}