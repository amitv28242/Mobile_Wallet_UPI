package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;

public class Transaction implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("reference")
    private String reference;

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("transactionType")
    private String transactionType; // CREDIT or DEBIT

    @SerializedName("status")
    private String status;

    @SerializedName("balanceBefore")
    private BigDecimal balanceBefore;

    @SerializedName("balanceAfter")
    private BigDecimal balanceAfter;

    @SerializedName("description")
    private String description;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("payment")
    private PaymentInfo payment;

    public Transaction() {}

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public BigDecimal getAmount() { return amount != null ? amount : BigDecimal.ZERO; }
    public String getTransactionType() { return transactionType; }
    public String getStatus() { return status; }
    public BigDecimal getBalanceBefore() { return balanceBefore; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public String getCreatedAt() { return createdAt; }
    public PaymentInfo getPayment() { return payment; }

    public boolean isCredit() {
        return "CREDIT".equalsIgnoreCase(transactionType);
    }

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }

    public String getFormattedAmount() {
        String sign = isCredit() ? "+" : "-";
        return sign + "₹" + (amount != null ? amount.toPlainString() : "0.00");
    }

    public static class PaymentInfo implements Serializable {
        @SerializedName("id")
        private Long id;

        @SerializedName("referenceId")
        private String referenceId;

        @SerializedName("paymentType")
        private String paymentType;

        @SerializedName("payer")
        private PartyInfo payer;

        @SerializedName("payee")
        private PartyInfo payee;

        public Long getId() { return id; }
        public String getReferenceId() { return referenceId; }
        public String getPaymentType() { return paymentType; }
        public PartyInfo getPayer() { return payer; }
        public PartyInfo getPayee() { return payee; }
    }

    public static class PartyInfo implements Serializable {
        @SerializedName("id")
        private Long id;

        @SerializedName("fullName")
        private String fullName;

        public Long getId() { return id; }
        public String getFullName() { return fullName; }
    }
}