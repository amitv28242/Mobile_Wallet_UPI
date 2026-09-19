// FILE: src/main/java/com/mobilewallet/validator/TransactionValidator.java
package com.mobilewallet.validator;

import com.mobilewallet.entity.Payment;
import com.mobilewallet.entity.Wallet;
import com.mobilewallet.exception.InsufficientBalanceException;
import com.mobilewallet.exception.InvalidPaymentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionValidator {

    public void validatePayment(Payment payment) {
        if (payment == null) {
            throw new InvalidPaymentException("Payment cannot be null");
        }

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Payment amount must be greater than zero");
        }

        if (payment.getPayer() == null || payment.getPayee() == null) {
            throw new InvalidPaymentException("Payer and payee must be specified");
        }

        if (payment.getPayer().getId().equals(payment.getPayee().getId())) {
            throw new InvalidPaymentException("Cannot make payment to yourself");
        }
    }

    public void validateWalletBalance(Wallet wallet, BigDecimal amount) {
        if (wallet == null) {
            throw new InvalidPaymentException("Wallet not found");
        }

        if (!wallet.isActive()) {
            throw new InvalidPaymentException("Wallet is not active");
        }

        if (!wallet.hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: " + wallet.getBalance() + 
                    ", Required: " + amount
            );
        }
    }

    public void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidPaymentException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Amount must be greater than zero");
        }

        if (amount.scale() > 2) {
            throw new InvalidPaymentException("Amount cannot have more than 2 decimal places");
        }
    }

    public void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey != null && idempotencyKey.length() > 100) {
            throw new InvalidPaymentException("Idempotency key cannot exceed 100 characters");
        }
    }
}