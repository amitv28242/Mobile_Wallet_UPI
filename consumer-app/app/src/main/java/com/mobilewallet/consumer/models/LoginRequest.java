package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;

public record LoginRequest(@SerializedName("usernameOrEmail") String usernameOrEmail,
                           @SerializedName("password") String password) {

    @Override
    public String usernameOrEmail() {
        return usernameOrEmail;
    }

    @Override
    public String password() {
        return password;
    }
}