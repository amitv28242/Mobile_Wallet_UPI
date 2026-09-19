package com.mobilewallet.exception;

public class InvalidPaymentException extends ApiException {
    public InvalidPaymentException(String message) {
        super(message, "INVALID_PAYMENT");
    }
}
