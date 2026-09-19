// FILE: src/main/java/com/mobilewallet/util/ReferenceNumberGenerator.java
package com.mobilewallet.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ReferenceNumberGenerator {

    private static final AtomicLong counter = new AtomicLong(1);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String generateReference(String prefix) {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        long seq = counter.getAndIncrement() % 1000000;
        String seqStr = String.format("%06d", seq);
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        return prefix + date + seqStr + uuid;
    }

    public String generateTransactionReference() {
        return generateReference("TXN");
    }

    public String generatePaymentReference() {
        return generateReference("PAY");
    }

    public String generateQRReference() {
        return generateReference("QR");
    }

    public String generateIdempotencyKey() {
        return UUID.randomUUID().toString();
    }
}