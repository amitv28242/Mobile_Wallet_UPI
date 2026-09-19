package com.mobilewallet.exception;

public class PaymentProcessingException extends ApiException {
    public PaymentProcessingException(String message) {
        super(message, "PAYMENT_FAILED");
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause, "PAYMENT_FAILED");
    }
}
