package com.mobilewallet.exception;

public class AccountLockedException extends ApiException {
    public AccountLockedException(String message) {
        super(message, "ACCOUNT_LOCKED");
    }
}
