package com.mobilewallet.dto.admin;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private WalletInfo wallet;
    private MerchantInfo merchant;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MerchantInfo {
        private Long id;
        private String businessName;
        private String businessType;
        private Boolean verified;
        private String businessAddress;
        private String businessPhone;
    }
}

    