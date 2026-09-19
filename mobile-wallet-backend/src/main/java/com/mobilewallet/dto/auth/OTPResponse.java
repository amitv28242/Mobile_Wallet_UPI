// FILE: src/main/java/com/mobilewallet/dto/auth/OTPResponse.java
package com.mobilewallet.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPResponse {
    private String identifier;
    private String purpose;
    private LocalDateTime expiresAt;
    private String message;
    private Boolean success;
    private Integer remainingAttempts;
    private Long cooldownSeconds;
}