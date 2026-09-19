package com.mobilewallet.exception;

public class GuestLoginDisabledException extends ApiException {
    public GuestLoginDisabledException(String message) {
        super(message, "GUEST_LOGIN_DISABLED");
    }
}