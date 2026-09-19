// FILE: src/main/java/com/mobilewallet/service/AuthService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.auth.*;
import com.mobilewallet.entity.User;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);

    OTPResponse generateOTP(String identifier, String purpose);

    boolean verifyOTP(OTPRequest request);

    OTPResponse resendOTP(String identifier, String purpose);

    AuthResponse guestLogin(GuestLoginRequest request);

    User getCurrentUser();

    boolean validateToken(String token);
}