package com.mobilewallet.exception;

public class InsufficientBalanceException extends ApiException {
    public InsufficientBalanceException(String message) {
        super(message, "INSUFFICIENT_BALANCE");
    }
}
