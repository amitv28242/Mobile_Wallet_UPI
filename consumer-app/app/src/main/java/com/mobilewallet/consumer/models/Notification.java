package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Notification implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("isRead")
    private Boolean isRead;

    @SerializedName("data")
    private String data;

    @SerializedName("createdAt")
    private String createdAt;

    public Notification() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public Boolean getIsRead() { return isRead != null ? isRead : false; }
    public String getData() { return data; }
    public String getCreatedAt() { return createdAt; }

    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}