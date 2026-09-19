package com.mobilewallet.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String errorCode;

    public ApiException(String message) {
        super(message);
        this.errorCode = "API_ERROR";
    }

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
