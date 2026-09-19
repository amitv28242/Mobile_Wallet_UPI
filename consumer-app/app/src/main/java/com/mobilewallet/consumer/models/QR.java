package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;

public class QR implements Serializable {

    @SerializedName("qrToken")
    private String qrToken;

    @SerializedName("qrPayload")
    private String qrPayload;

    @SerializedName("qrImage")
    private String qrImage; // Base64 encoded

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("description")
    private String description;

    @SerializedName("status")
    private String status;

    @SerializedName("expiresAt")
    private String expiresAt;

    @SerializedName("userId")
    private String userId;

    @SerializedName("userFullName")
    private String userFullName;

    public QR() {}

    public String getQrToken() { return qrToken; }
    public String getQrPayload() { return qrPayload; }
    public String getQrImage() { return qrImage; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getExpiresAt() { return expiresAt; }
    public String getUserId() { return userId; }
    public String getUserFullName() { return userFullName; }

    public void setQrToken(String qrToken) { this.qrToken = qrToken; }
    public void setQrPayload(String qrPayload) { this.qrPayload = qrPayload; }
    public void setQrImage(String qrImage) { this.qrImage = qrImage; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
}