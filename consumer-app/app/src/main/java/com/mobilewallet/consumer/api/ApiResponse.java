package com.mobilewallet.consumer.api;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private String code;

    @SerializedName("data")
    private T data;

    @SerializedName("timestamp")
    private String timestamp;

    public ApiResponse() {}

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getCode() { return code; }
    public T getData() { return data; }
    public String getTimestamp() { return timestamp; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setCode(String code) { this.code = code; }
    public void setData(T data) { this.data = data; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}