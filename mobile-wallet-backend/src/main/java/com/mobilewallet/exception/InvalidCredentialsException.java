package com.mobilewallet.exception;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS");
    }
}