package com.mobilewallet.exception;

public class InvalidOTPException extends ApiException {
    public InvalidOTPException(String message) {
        super(message, "INVALID_OTP");
    }
}