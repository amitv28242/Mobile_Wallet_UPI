// FILE: src/main/java/com/mobilewallet/dto/auth/AuthResponse.java
package com.mobilewallet.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String firstName;
        private String lastName;
        private String fullName;
        private String role;
        private Boolean enabled;
        private Boolean locked;
        private WalletInfo wallet;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletInfo {
        private Long id;
        private String walletNumber;
        private String balance;
        private String currency;
        private String status;
    }
}