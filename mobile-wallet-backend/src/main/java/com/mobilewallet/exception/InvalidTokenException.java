package com.mobilewallet.exception;

public class InvalidTokenException extends ApiException {
    public InvalidTokenException(String message) {
        super(message, "INVALID_TOKEN");
    }
}