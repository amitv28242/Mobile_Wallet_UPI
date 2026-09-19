package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;

public class Wallet implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("walletNumber")
    private String walletNumber;

    @SerializedName("balance")
    private BigDecimal balance;

    @SerializedName("currency")
    private String currency;

    @SerializedName("status")
    private String status;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("userFullName")
    private String userFullName;

    public Wallet() {}

    public Long getId() { return id; }
    public String getWalletNumber() { return walletNumber; }
    public BigDecimal getBalance() { return balance != null ? balance : BigDecimal.ZERO; }
    public String getCurrency() { return currency != null ? currency : "INR"; }
    public String getStatus() { return status; }
    public Long getUserId() { return userId; }
    public String getUserFullName() { return userFullName; }

    public void setId(Long id) { this.id = id; }
    public void setWalletNumber(String walletNumber) { this.walletNumber = walletNumber; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStatus(String status) { this.status = status; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getFormattedBalance() {
        return "₹" + (balance != null ? balance.toPlainString() : "0.00");
    }
}