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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QRTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "qr_token", nullable = false, unique = true, length = 100)
    private String qrToken;

    @NotBlank
    @Column(name = "qr_payload", nullable = false, columnDefinition = "TEXT")
    private String qrPayload;

    @DecimalMin("0.0000")
    @Column(precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QRStatus status = QRStatus.ACTIVE;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum QRStatus {
        ACTIVE,
        SCANNED,
        EXPIRED,
        USED
    }

    public boolean isActive() {
        return status == QRStatus.ACTIVE && LocalDateTime.now().isBefore(expiresAt);
    }

    public boolean isExpired() {
        return status == QRStatus.EXPIRED || LocalDateTime.now().isAfter(expiresAt);
    }
}