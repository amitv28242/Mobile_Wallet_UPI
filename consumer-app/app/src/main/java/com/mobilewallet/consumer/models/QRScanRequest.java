package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;

public record QRScanRequest(@SerializedName("qrPayload") String qrPayload,
                            @SerializedName("idempotencyKey") String idempotencyKey) {

    @Override
    public String qrPayload() {
        return qrPayload;
    }

    @Override
    public String idempotencyKey() {
        return idempotencyKey;
    }
}