package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class BillPaymentRequest {

    @SerializedName("billType")
    private String billType;

    @SerializedName("billerCode")
    private String billerCode;

    @SerializedName("consumerNumber")
    private String consumerNumber;

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("dueDate")
    private String dueDate;

    @SerializedName("description")
    private String description;

    public BillPaymentRequest() {}

    public String getBillType() { return billType; }
    public String getBillerCode() { return billerCode; }
    public String getConsumerNumber() { return consumerNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getDueDate() { return dueDate; }
    public String getDescription() { return description; }

    public void setBillType(String billType) { this.billType = billType; }
    public void setBillerCode(String billerCode) { this.billerCode = billerCode; }
    public void setConsumerNumber(String consumerNumber) { this.consumerNumber = consumerNumber; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setDescription(String description) { this.description = description; }
}