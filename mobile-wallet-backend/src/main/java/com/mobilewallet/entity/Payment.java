package com.mobilewallet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payee_id", nullable = false)
    private User payee;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @NotNull
    @DecimalMin("0.0100")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.INITIATED;

    @NotBlank
    @Column(name = "reference_id", nullable = false, unique = true, length = 50)
    private String referenceId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum PaymentType {
        QR_PAYMENT,
        WALLET_TRANSFER,
        RECHARGE,
        BILL_PAYMENT
    }

    public enum PaymentStatus {
        INITIATED,
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED,
        EXPIRED,
        REFUNDED
    }

    public boolean isSuccess() {
        return status == PaymentStatus.SUCCESS;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    public boolean isExpired() {
        return status == PaymentStatus.EXPIRED;
    }

    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED;
    }

    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
        this.completedAt = LocalDateTime.now();
    }

    public void markCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }

    public void markExpired() {
        this.status = PaymentStatus.EXPIRED;
        this.completedAt = LocalDateTime.now();
    }
}