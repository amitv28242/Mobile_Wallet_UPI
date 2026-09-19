
USE mobile_wallet;

-- =====================
-- ADMIN USER (password: Admin@123)
-- =====================
INSERT INTO users (username, email, phone, password_hash, first_name, last_name, role, enabled, locked)
VALUES ('admin', 'admin@mobilewallet.com', '+919999999999', 
        '$2a$12$N9qB8jK4pQ5R6sT7uV8wX9yZ0aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uV1wX2yZ3',
        'System', 'Administrator', 'ROLE_ADMIN', TRUE, FALSE);

INSERT INTO admins (user_id, admin_level)
VALUES (LAST_INSERT_ID(), 'SUPER_ADMIN');

-- =====================
-- CONSUMER USER (password: Consumer@123)
-- =====================
INSERT INTO users (username, email, phone, password_hash, first_name, last_name, role, enabled, locked)
VALUES ('consumer1', 'consumer1@example.com', '+919888888888',
        '$2a$12$N9qB8jK4pQ5R6sT7uV8wX9yZ0aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uV1wX2yZ3',
        'Rahul', 'Sharma', 'ROLE_CONSUMER', TRUE, FALSE);

SET @consumer_id = LAST_INSERT_ID();

INSERT INTO wallets (user_id, balance, wallet_number, status, version)
VALUES (@consumer_id, 5000.0000, 'WLT' || LPAD(@consumer_id, 8, '0'), 'ACTIVE', 0);

-- =====================
-- MERCHANT USER (password: Merchant@123)
-- =====================
INSERT INTO users (username, email, phone, password_hash, first_name, last_name, role, enabled, locked)
VALUES ('merchant1', 'merchant1@example.com', '+917777777777',
        '$2a$12$N9qB8jK4pQ5R6sT7uV8wX9yZ0aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uV1wX2yZ3',
        'Priya', 'Patel', 'ROLE_MERCHANT', TRUE, FALSE);

SET @merchant_id = LAST_INSERT_ID();

INSERT INTO merchants (user_id, business_name, business_type, business_address, business_phone, verified)
VALUES (@merchant_id, 'Tech Solutions Pvt Ltd', 'IT Services', '123 Tech Park, Bangalore, India', '+919876543210', TRUE);

INSERT INTO wallets (user_id, balance, wallet_number, status, version)
VALUES (@merchant_id, 25000.0000, 'WLT' || LPAD(@merchant_id, 8, '0'), 'ACTIVE', 0);

-- =====================
-- ADDITIONAL CONSUMER
-- =====================
INSERT INTO users (username, email, phone, password_hash, first_name, last_name, role, enabled, locked)
VALUES ('consumer2', 'consumer2@example.com', '+916666666666',
        '$2a$12$N9qB8jK4pQ5R6sT7uV8wX9yZ0aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uV1wX2yZ3',
        'Amit', 'Verma', 'ROLE_CONSUMER', TRUE, FALSE);

SET @consumer2_id = LAST_INSERT_ID();

INSERT INTO wallets (user_id, balance, wallet_number, status, version)
VALUES (@consumer2_id, 1000.0000, 'WLT' || LPAD(@consumer2_id, 8, '0'), 'ACTIVE', 0);

-- =====================
-- SAMPLE TRANSACTIONS
-- =====================
-- Payment: Consumer1 -> Merchant1
INSERT INTO payments (payer_id, payee_id, wallet_id, amount, payment_type, status, reference_id, description, completed_at)
VALUES (@consumer_id, @merchant_id, 
        (SELECT id FROM wallets WHERE user_id = @consumer_id),
        500.0000, 'QR_PAYMENT', 'SUCCESS', 
        'PAY' || LPAD(FLOOR(RAND() * 1000000), 6, '0'),
        'Payment for services', NOW());

SET @payment_id = LAST_INSERT_ID();

-- Debit transaction for consumer
INSERT INTO transaction_history (payment_id, wallet_id, user_id, amount, transaction_type, status, reference, balance_before, balance_after, description)
VALUES (@payment_id, 
        (SELECT id FROM wallets WHERE user_id = @consumer_id),
        @consumer_id,
        500.0000, 'DEBIT', 'SUCCESS',
        'PAY' || LPAD(FLOOR(RAND() * 1000000), 6, '0'),
        5000.0000, 4500.0000,
        'Payment to Tech Solutions Pvt Ltd');

-- Credit transaction for merchant
INSERT INTO transaction_history (payment_id, wallet_id, user_id, amount, transaction_type, status, reference, balance_before, balance_after, description)
VALUES (@payment_id,
        (SELECT id FROM wallets WHERE user_id = @merchant_id),
        @merchant_id,
        500.0000, 'CREDIT', 'SUCCESS',
        'PAY' || LPAD(FLOOR(RAND() * 1000000), 6, '0'),
        25000.0000, 25500.0000,
        'Payment from Rahul Sharma');

-- =====================
-- SAMPLE NOTIFICATIONS
-- =====================
INSERT INTO notifications (user_id, title, message, type, is_read)
VALUES 
    (@consumer_id, 'Payment Sent', 'You have sent ₹500.00 to Tech Solutions Pvt Ltd', 'PAYMENT_SENT', FALSE),
    (@merchant_id, 'Payment Received', 'You have received ₹500.00 from Rahul Sharma', 'PAYMENT_RECEIVED', FALSE),
    (@consumer_id, 'Wallet Recharge', 'Your wallet has been recharged with ₹1000.00', 'WALLET_RECHARGE', TRUE);

-- =====================
-- UPDATE WALLET BALANCES
-- =====================
UPDATE wallets SET balance = 4500.0000, version = version + 1 WHERE user_id = @consumer_id;
UPDATE wallets SET balance = 25500.0000, version = version + 1 WHERE user_id = @merchant_id;

-- =====================
-- Sample QR Transactions
-- =====================
INSERT INTO qr_transactions (user_id, qr_token, qr_payload, amount, status, expires_at)
VALUES (@merchant_id,
        'QR' || REPLACE(UUID(), '-', ''),
        'qr_payload_encrypted_data',
        500.0000,
        'ACTIVE',
        DATE_ADD(NOW(), INTERVAL 5 MINUTE));