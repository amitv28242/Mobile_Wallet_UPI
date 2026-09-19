package com.mobilewallet.exception;

public class UnauthorizedAccessException extends ApiException {
    public UnauthorizedAccessException(String message) {
        super(message, "UNAUTHORIZED_ACCESS");
    }
}
