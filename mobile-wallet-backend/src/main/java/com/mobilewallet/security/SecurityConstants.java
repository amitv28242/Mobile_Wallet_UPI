// FILE: src/main/java/com/mobilewallet/security/SecurityConstants.java
package com.mobilewallet.security;

public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    public static final String TOKEN_TYPE = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";
    
    public static final String ROLE_CONSUMER = "ROLE_CONSUMER";
    public static final String ROLE_MERCHANT = "ROLE_MERCHANT";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final long ACCESS_TOKEN_EXPIRATION = 900; // 15 minutes
    public static final long REFRESH_TOKEN_EXPIRATION = 604800; // 7 days
    public static final long OTP_EXPIRATION = 300; // 5 minutes
}