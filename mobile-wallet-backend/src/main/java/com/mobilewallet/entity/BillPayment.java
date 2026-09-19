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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BillPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "bill_type", nullable = false, length = 50)
    private String billType;

    @NotBlank
    @Column(name = "biller_code", nullable = false, length = 50)
    private String billerCode;

    @NotBlank
    @Column(name = "consumer_number", nullable = false, length = 100)
    private String consumerNumber;

    @NotNull
    @DecimalMin("1.0000")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status = BillStatus.PENDING;

    @NotBlank
    @Column(name = "reference_id", nullable = false, unique = true, length = 50)
    private String referenceId;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum BillStatus {
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED
    }
}