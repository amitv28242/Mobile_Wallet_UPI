// FILE: backend/src/main/java/com/mobilewallet/dto/auth/RegisterResponse.java
package com.mobilewallet.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response returned after a successful or failed registration attempt.
 * Contains sanitized user information (never passwords, never tokens).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {

    private Boolean success;
    private String message;

    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String role;

    private Boolean requiresOTPVerification;
    private String otpIdentifier;      // phone or email used for OTP
    private String otpPurpose;         // e.g., "REGISTRATION"
    private Integer otpExpiresInSeconds;

    private WalletInfo wallet;
    private LocalDateTime createdAt;

    // ============================================================
    // Nested DTOs
    // ============================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WalletInfo {
        private Long id;
        private String walletNumber;
        private String balance;
        private String currency;
        private String status;
    }
}