package com.mobilewallet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank
    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @NotBlank
    @Column(name = "business_type", nullable = false, length = 50)
    private String businessType;

    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @NotBlank
    @Column(name = "business_address", nullable = false, columnDefinition = "TEXT")
    private String businessAddress;

    @NotBlank
    @Column(name = "business_phone", nullable = false, length = 20)
    private String businessPhone;

    @Column(nullable = false)
    private Boolean verified = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}