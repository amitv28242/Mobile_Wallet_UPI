package com.mobilewallet.exception;

public class DuplicatePaymentException extends ApiException {
    public DuplicatePaymentException(String message) {
        super(message, "DUPLICATE_PAYMENT");
    }
}
