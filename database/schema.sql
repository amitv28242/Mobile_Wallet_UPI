CREATE DATABASE IF NOT EXISTS mobile_wallet

CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE mobile_wallet;
-- =====================
-- USERS TABLE
-- =====================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('ROLE_CONSUMER', 'ROLE_MERCHANT', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_CONSUMER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_users_email (email),
    INDEX idx_users_phone (phone),
    INDEX idx_users_role (role),
    INDEX idx_users_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- MERCHANTS TABLE
-- =====================
CREATE TABLE merchants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    business_name VARCHAR(100) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    gst_number VARCHAR(20),
    business_address TEXT NOT NULL,
    business_phone VARCHAR(20) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_merchants_user_id (user_id),
    INDEX idx_merchants_business_name (business_name),
    INDEX idx_merchants_verified (verified)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- WALLETS TABLE
-- =====================
CREATE TABLE wallets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0.0000,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    wallet_number VARCHAR(20) NOT NULL UNIQUE,
    status ENUM('ACTIVE', 'FROZEN', 'CLOSED') NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_wallets_user_id (user_id),
    INDEX idx_wallets_wallet_number (wallet_number),
    INDEX idx_wallets_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- CARDS TABLE
-- =====================
CREATE TABLE cards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    card_number_masked VARCHAR(20) NOT NULL,
    cardholder_name VARCHAR(100) NOT NULL,
    expiry_month VARCHAR(2) NOT NULL,
    expiry_year VARCHAR(4) NOT NULL,
    issuer VARCHAR(50) NOT NULL,
    last_four VARCHAR(4) NOT NULL,
    encrypted_data TEXT NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_cards_user_id (user_id),
    INDEX idx_cards_last_four (last_four)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- BANK ACCOUNTS TABLE
-- =====================
CREATE TABLE bank_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    account_number_masked VARCHAR(20) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    account_holder_name VARCHAR(100) NOT NULL,
    encrypted_data TEXT NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_bank_accounts_user_id (user_id),
    INDEX idx_bank_accounts_ifsc (ifsc_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- PAYMENTS TABLE
-- =====================
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payer_id BIGINT NOT NULL,
    payee_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    payment_type ENUM('QR_PAYMENT', 'WALLET_TRANSFER', 'RECHARGE', 'BILL_PAYMENT') NOT NULL,
    status ENUM('INITIATED', 'PENDING', 'SUCCESS', 'FAILED', 'CANCELLED', 'EXPIRED', 'REFUNDED') NOT NULL DEFAULT 'INITIATED',
    reference_id VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    idempotency_key VARCHAR(100) UNIQUE,
    expires_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (payer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (payee_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id),
    
    INDEX idx_payments_payer_id (payer_id),
    INDEX idx_payments_payee_id (payee_id),
    INDEX idx_payments_status (status),
    INDEX idx_payments_reference_id (reference_id),
    INDEX idx_payments_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- QR TRANSACTIONS TABLE
-- =====================
CREATE TABLE qr_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    qr_token VARCHAR(100) NOT NULL UNIQUE,
    qr_payload TEXT NOT NULL,
    amount DECIMAL(19,4),
    status ENUM('ACTIVE', 'SCANNED', 'EXPIRED', 'USED') NOT NULL DEFAULT 'ACTIVE',
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_qr_transactions_user_id (user_id),
    INDEX idx_qr_transactions_qr_token (qr_token),
    INDEX idx_qr_transactions_status (status),
    INDEX idx_qr_transactions_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- TRANSACTION HISTORY TABLE
-- =====================
CREATE TABLE transaction_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT,
    wallet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    transaction_type ENUM('CREDIT', 'DEBIT') NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'SUCCESS',
    reference VARCHAR(50) NOT NULL,
    balance_before DECIMAL(19,4) NOT NULL,
    balance_after DECIMAL(19,4) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE SET NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    
    INDEX idx_transaction_history_wallet_id (wallet_id),
    INDEX idx_transaction_history_user_id (user_id),
    INDEX idx_transaction_history_payment_id (payment_id),
    INDEX idx_transaction_history_reference (reference),
    INDEX idx_transaction_history_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- NOTIFICATIONS TABLE
-- =====================
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    data JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_is_read (is_read),
    INDEX idx_notifications_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- REFRESH TOKENS TABLE
-- =====================
CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_token (token),
    INDEX idx_refresh_tokens_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- OTP VERIFICATIONS TABLE
-- =====================
CREATE TABLE otp_verifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    identifier VARCHAR(100) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_otp_verifications_user_id (user_id),
    INDEX idx_otp_verifications_identifier (identifier),
    INDEX idx_otp_verifications_otp_code (otp_code),
    INDEX idx_otp_verifications_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- BILL PAYMENTS TABLE
-- =====================
CREATE TABLE bill_payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    bill_type VARCHAR(50) NOT NULL,
    biller_code VARCHAR(50) NOT NULL,
    consumer_number VARCHAR(100) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    reference_id VARCHAR(50) NOT NULL UNIQUE,
    due_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_bill_payments_user_id (user_id),
    INDEX idx_bill_payments_reference_id (reference_id),
    INDEX idx_bill_payments_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- RECHARGE TRANSACTIONS TABLE
-- =====================
CREATE TABLE recharge_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    operator VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    reference_id VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_recharge_transactions_user_id (user_id),
    INDEX idx_recharge_transactions_reference_id (reference_id),
    INDEX idx_recharge_transactions_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- ADMINS TABLE
-- =====================
CREATE TABLE admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    admin_level ENUM('SUPER_ADMIN', 'MODERATOR', 'SUPPORT') NOT NULL DEFAULT 'MODERATOR',
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_admins_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================
-- AUDIT LOGS TABLE
-- =====================
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    INDEX idx_audit_logs_admin_id (admin_id),
    INDEX idx_audit_logs_user_id (user_id),
    INDEX idx_audit_logs_action (action),
    INDEX idx_audit_logs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


