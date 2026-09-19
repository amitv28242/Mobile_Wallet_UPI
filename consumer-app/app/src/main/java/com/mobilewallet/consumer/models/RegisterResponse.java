// FILE: consumer-app/app/src/main/java/com/mobilewallet/consumer/models/RegisterResponse.java
package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RegisterResponse implements Serializable {

    @SerializedName("success") private Boolean success;
    @SerializedName("message") private String message;
    @SerializedName("userId") private Long userId;
    @SerializedName("username") private String username;
    @SerializedName("email") private String email;
    @SerializedName("phone") private String phone;
    @SerializedName("firstName") private String firstName;
    @SerializedName("lastName") private String lastName;
    @SerializedName("role") private String role;
    @SerializedName("requiresOTPVerification") private Boolean requiresOTPVerification;
    @SerializedName("otpIdentifier") private String otpIdentifier;
    @SerializedName("otpPurpose") private String otpPurpose;
    @SerializedName("otpExpiresInSeconds") private Integer otpExpiresInSeconds;
    @SerializedName("wallet") private WalletInfo wallet;
    @SerializedName("createdAt") private String createdAt;

    public RegisterResponse() {}

    public boolean isSuccess() { return Boolean.TRUE.equals(success); }
    public String getMessage() { return message; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public Boolean getRequiresOTPVerification() { return requiresOTPVerification; }
    public String getOtpIdentifier() { return otpIdentifier; }
    public String getOtpPurpose() { return otpPurpose; }
    public Integer getOtpExpiresInSeconds() { return otpExpiresInSeconds; }
    public WalletInfo getWallet() { return wallet; }
    public String getCreatedAt() { return createdAt; }

    public static class WalletInfo implements Serializable {
        @SerializedName("id") private Long id;
        @SerializedName("walletNumber") private String walletNumber;
        @SerializedName("balance") private String balance;
        @SerializedName("currency") private String currency;
        @SerializedName("status") private String status;

        public Long getId() { return id; }
        public String getWalletNumber() { return walletNumber; }
        public String getBalance() { return balance; }
        public String getCurrency() { return currency; }
        public String getStatus() { return status; }
    }
}