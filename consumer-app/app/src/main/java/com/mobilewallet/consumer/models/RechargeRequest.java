package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class RechargeRequest {

    @SerializedName("operator")
    private final String operator;

    @SerializedName("phoneNumber")
    private final String phoneNumber;

    @SerializedName("amount")
    private final BigDecimal amount;

    @SerializedName("description")
    private String description;

    public RechargeRequest(String operator, String phoneNumber, BigDecimal amount) {
        this.operator = operator;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
    }

    public String getOperator() { return operator; }
    public String getPhoneNumber() { return phoneNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
}