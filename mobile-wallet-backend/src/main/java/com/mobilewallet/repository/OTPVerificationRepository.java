// FILE: src/main/java/com/mobilewallet/repository/OTPVerificationRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {

    @Query("SELECT o FROM OTPVerification o WHERE o.identifier = :identifier AND o.purpose = :purpose AND o.verified = false ORDER BY o.createdAt DESC")
    List<OTPVerification> findUnverifiedByIdentifierAndPurpose(@Param("identifier") String identifier, 
                                                               @Param("purpose") String purpose);

    @Query("SELECT o FROM OTPVerification o WHERE o.identifier = :identifier AND o.purpose = :purpose AND o.otpCode = :otpCode AND o.verified = false AND o.expiresAt > :now")
    Optional<OTPVerification> findValidOTP(@Param("identifier") String identifier, 
                                           @Param("purpose") String purpose,
                                           @Param("otpCode") String otpCode,
                                           @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE OTPVerification o SET o.verified = true WHERE o.id = :id")
    int markAsVerified(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE OTPVerification o SET o.attempts = o.attempts + 1 WHERE o.id = :id")
    int incrementAttempts(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM OTPVerification o WHERE o.expiresAt < :now OR o.verified = true")
    int deleteExpiredOrVerified(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(o) FROM OTPVerification o WHERE o.identifier = :identifier AND o.createdAt > :since")
    long countRecentOTPs(@Param("identifier") String identifier, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(o) FROM OTPVerification o WHERE o.user.id = :userId AND o.verified = false AND o.expiresAt > :now")
    long countActiveOTPsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}