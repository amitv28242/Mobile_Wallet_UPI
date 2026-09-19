package com.mobilewallet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "card_number_masked", nullable = false, length = 20)
    private String cardNumberMasked;

    @NotBlank
    @Column(name = "cardholder_name", nullable = false, length = 100)
    private String cardholderName;

    @NotBlank
    @Column(name = "expiry_month", nullable = false, length = 2)
    private String expiryMonth;

    @NotBlank
    @Column(name = "expiry_year", nullable = false, length = 4)
    private String expiryYear;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String issuer;

    @NotBlank
    @Column(name = "last_four", nullable = false, length = 4)
    private String lastFour;

    @NotBlank
    @Column(name = "encrypted_data", nullable = false, columnDefinition = "TEXT")
    private String encryptedData;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}