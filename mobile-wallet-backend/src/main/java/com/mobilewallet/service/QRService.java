// FILE: src/main/java/com/mobilewallet/service/QRService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.qr.QRGenerateRequest;
import com.mobilewallet.dto.qr.QRGenerateResponse;
import com.mobilewallet.entity.QRTransaction;

public interface QRService {

    QRGenerateResponse generateQR(Long userId, QRGenerateRequest request);

    QRTransaction validateQR(String qrPayload);

    void markQRUsed(String qrToken);

    boolean isQRValid(String qrToken);

    void cleanupExpiredQRCodes();
}