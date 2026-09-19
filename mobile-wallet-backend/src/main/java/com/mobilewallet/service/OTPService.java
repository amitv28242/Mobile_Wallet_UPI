// FILE: src/main/java/com/mobilewallet/service/OTPService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.auth.OTPResponse;

public interface OTPService {

    OTPResponse generateAndSendOTP(String identifier, String purpose);

    boolean verifyOTP(String identifier, String purpose, String otpCode);

    OTPResponse resendOTP(String identifier, String purpose);

    void invalidateOTP(String identifier, String purpose);

    void cleanupExpiredOTPs();

    boolean isOTPRateLimited(String identifier);
}