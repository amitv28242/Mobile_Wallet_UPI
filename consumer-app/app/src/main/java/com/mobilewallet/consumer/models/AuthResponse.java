package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AuthResponse implements Serializable {

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("tokenType")
    private String tokenType;

    @SerializedName("expiresIn")
    private Long expiresIn;

    @SerializedName("user")
    private User user;

    public AuthResponse() {}

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public Long getExpiresIn() { return expiresIn; }
    public User getUser() { return user; }

    public boolean isSuccess() {
        return accessToken != null && !accessToken.isEmpty();
    }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    public void setUser(User user) { this.user = user; }
}