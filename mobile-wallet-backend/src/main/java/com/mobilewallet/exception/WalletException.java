package com.mobilewallet.exception;

public class WalletException extends ApiException {
    public WalletException(String message) {
        super(message, "WALLET_ERROR");
    }
}