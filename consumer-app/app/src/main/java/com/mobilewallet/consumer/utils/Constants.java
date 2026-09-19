package com.mobilewallet.consumer.utils;

public final class Constants {

    private Constants() {}

    // Notification Channels
    public static final String CHANNEL_PAYMENTS = "payments_channel";
    public static final String CHANNEL_SECURITY = "security_channel";
    public static final String CHANNEL_GENERAL = "general_channel";

    // Intent Extra Keys
    public static final String EXTRA_QR_PAYLOAD = "extra_qr_payload";
    public static final String EXTRA_PAYMENT = "extra_payment";
    public static final String EXTRA_TRANSACTION = "extra_transaction";
    public static final String EXTRA_AMOUNT = "extra_amount";
    public static final String EXTRA_USER = "extra_user";
    public static final String EXTRA_NOTIFICATION = "extra_notification";

    // Request Codes
    public static final int REQUEST_CAMERA_PERMISSION = 1001;
    public static final int REQUEST_NOTIFICATION_PERMISSION = 1002;
    public static final int REQUEST_QR_SCAN = 1003;
    public static final int REQUEST_BIOMETRIC = 1004;

    // OTP Purposes
    public static final String OTP_PURPOSE_REGISTRATION = "REGISTRATION";
    public static final String OTP_PURPOSE_LOGIN = "LOGIN";
    public static final String OTP_PURPOSE_PAYMENT = "PAYMENT";
    public static final String OTP_PURPOSE_RESET_PASSWORD = "RESET_PASSWORD";

    // Payment Statuses
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Transaction Types
    public static final String TRANSACTION_CREDIT = "CREDIT";
    public static final String TRANSACTION_DEBIT = "DEBIT";

    // Roles
    public static final String ROLE_CONSUMER = "ROLE_CONSUMER";
    public static final String ROLE_MERCHANT = "ROLE_MERCHANT";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // Amount limits
    public static final double MIN_ADD_MONEY = 1.0;
    public static final double MAX_ADD_MONEY = 100000.0;
    public static final double MIN_TRANSFER = 1.0;
    public static final double MAX_TRANSFER = 50000.0;

    // QR expiration
    public static final int QR_EXPIRY_SECONDS = 300;

    // API
    public static final int PAGE_SIZE = 20;
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
}