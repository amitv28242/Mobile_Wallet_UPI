package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class AddMoneyRequest {

    @SerializedName("amount")
    private final BigDecimal amount;

    @SerializedName("paymentMethod")
    private final String paymentMethod;

    @SerializedName("cardId")
    private String cardId;

    @SerializedName("bankAccountId")
    private String bankAccountId;

    public AddMoneyRequest(BigDecimal amount) {
        this.amount = amount;
        this.paymentMethod = "CARD";
    }

    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCardId() { return cardId; }
    public String getBankAccountId() { return bankAccountId; }
}