package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BankAccount implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("accountNumberMasked")
    private String accountNumberMasked;

    @SerializedName("ifscCode")
    private String ifscCode;

    @SerializedName("bankName")
    private String bankName;

    @SerializedName("accountHolderName")
    private String accountHolderName;

    @SerializedName("isDefault")
    private Boolean isDefault;

    @SerializedName("createdAt")
    private String createdAt;

    public BankAccount() {}

    public Long getId() { return id; }
    public String getAccountNumberMasked() { return accountNumberMasked; }
    public String getIfscCode() { return ifscCode; }
    public String getBankName() { return bankName; }
    public String getAccountHolderName() { return accountHolderName; }
    public Boolean getIsDefault() { return isDefault; }
    public String getCreatedAt() { return createdAt; }
}