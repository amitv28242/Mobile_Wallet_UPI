package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;

public record OTPRequest(@SerializedName("identifier") String identifier,
                         @SerializedName("purpose") String purpose,
                         @SerializedName("otpCode") String otpCode) {

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String purpose() {
        return purpose;
    }

    @Override
    public String otpCode() {
        return otpCode;
    }
}