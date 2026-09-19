// FILE: src/main/java/com/mobilewallet/util/QRCodeParser.java
package com.mobilewallet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class QRCodeParser {

    private final ObjectMapper objectMapper;

    public Map<String, Object> parseQRPayload(String qrPayload) {
        try {
            return objectMapper.readValue(qrPayload, Map.class);
        } catch (Exception e) {
            log.error("Failed to parse QR payload", e);
            throw new RuntimeException("Invalid QR payload format", e);
        }
    }

    public String getTokenFromPayload(String qrPayload) {
        Map<String, Object> payload = parseQRPayload(qrPayload);
        return (String) payload.get("token");
    }

    public String getUserIdFromPayload(String qrPayload) {
        Map<String, Object> payload = parseQRPayload(qrPayload);
        return (String) payload.get("userId");
    }

    public String getAmountFromPayload(String qrPayload) {
        Map<String, Object> payload = parseQRPayload(qrPayload);
        return (String) payload.get("amount");
    }

    public String getTypeFromPayload(String qrPayload) {
        Map<String, Object> payload = parseQRPayload(qrPayload);
        return (String) payload.get("type");
    }

    public boolean isQRExpired(String qrPayload) {
        Map<String, Object> payload = parseQRPayload(qrPayload);
        String timestamp = (String) payload.get("timestamp");
        if (timestamp == null) {
            return true;
        }
        // Check if QR is expired (5 minutes default)
        try {
            java.time.LocalDateTime qrTime = java.time.LocalDateTime.parse(timestamp);
            return java.time.LocalDateTime.now().isAfter(qrTime.plusMinutes(5));
        } catch (Exception e) {
            log.error("Failed to parse timestamp from QR payload", e);
            return true;
        }
    }
}