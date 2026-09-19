package com.mobilewallet.exception;

public class InvalidQRException extends ApiException {
    public InvalidQRException(String message) {
        super(message, "INVALID_QR");
    }
}
