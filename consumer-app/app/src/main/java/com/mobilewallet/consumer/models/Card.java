package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Card implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("cardNumberMasked")
    private String cardNumberMasked;

    @SerializedName("cardholderName")
    private String cardholderName;

    @SerializedName("expiryMonth")
    private String expiryMonth;

    @SerializedName("expiryYear")
    private String expiryYear;

    @SerializedName("issuer")
    private String issuer;

    @SerializedName("lastFour")
    private String lastFour;

    @SerializedName("isDefault")
    private Boolean isDefault;

    @SerializedName("createdAt")
    private String createdAt;

    public Card() {}

    public Long getId() { return id; }
    public String getCardNumberMasked() { return cardNumberMasked; }
    public String getCardholderName() { return cardholderName; }
    public String getExpiryMonth() { return expiryMonth; }
    public String getExpiryYear() { return expiryYear; }
    public String getIssuer() { return issuer; }
    public String getLastFour() { return lastFour; }
    public Boolean getIsDefault() { return isDefault; }
    public String getCreatedAt() { return createdAt; }

    public String getFormattedExpiry() {
        return (expiryMonth != null ? expiryMonth : "MM") + "/" +
                (expiryYear != null && expiryYear.length() >= 2 ?
                        expiryYear.substring(expiryYear.length() - 2) : "YY");
    }
}