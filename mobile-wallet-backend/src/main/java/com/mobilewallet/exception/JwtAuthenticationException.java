package com.mobilewallet.exception;

public class JwtAuthenticationException extends ApiException {
    public JwtAuthenticationException(String message) {
        super(message, "JWT_AUTH_ERROR");
    }
}