
USE mobile_wallet;

-- Users table
CREATE INDEX idx_users_created_at ON users(created_at);

-- Payments table - for reporting
CREATE INDEX idx_payments_status_created ON payments(status, created_at);
CREATE INDEX idx_payments_payer_status ON payments(payer_id, status);
CREATE INDEX idx_payments_payee_status ON payments(payee_id, status);

-- Transaction history - for user transaction listing
CREATE INDEX idx_transaction_history_user_status ON transaction_history(user_id, status, created_at DESC);

-- Notifications - for fetching unread count
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);

-- QR Transactions - for cleaning up expired tokens
CREATE INDEX idx_qr_transactions_status_expires ON qr_transactions(status, expires_at);

-- OTP Verifications - for cleanup
CREATE INDEX idx_otp_verifications_expires_verified ON otp_verifications(expires_at, verified);

-- Refresh Tokens - for cleanup
CREATE INDEX idx_refresh_tokens_expires ON refresh_tokens(expires_at);