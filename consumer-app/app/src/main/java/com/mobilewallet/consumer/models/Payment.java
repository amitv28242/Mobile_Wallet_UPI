package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;

public class Payment implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("referenceId")
    private String referenceId;

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("paymentType")
    private String paymentType;

    @SerializedName("status")
    private String status;

    @SerializedName("payer")
    private PartyInfo payer;

    @SerializedName("payee")
    private PartyInfo payee;

    @SerializedName("description")
    private String description;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("completedAt")
    private String completedAt;

    public Payment() {}

    public Long getId() { return id; }
    public String getReferenceId() { return referenceId; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentType() { return paymentType; }
    public String getStatus() { return status; }
    public PartyInfo getPayer() { return payer; }
    public PartyInfo getPayee() { return payee; }
    public String getDescription() { return description; }
    public String getCreatedAt() { return createdAt; }
    public String getCompletedAt() { return completedAt; }

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }

    public static class PartyInfo implements Serializable {
        @SerializedName("id")
        private Long id;

        @SerializedName("fullName")
        private String fullName;

        @SerializedName("walletNumber")
        private String walletNumber;

        public Long getId() { return id; }
        public String getFullName() { return fullName; }
        public String getWalletNumber() { return walletNumber; }
    }
}