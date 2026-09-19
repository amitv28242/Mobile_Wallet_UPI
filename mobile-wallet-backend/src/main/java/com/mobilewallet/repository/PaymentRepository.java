// FILE: src/main/java/com/mobilewallet/repository/PaymentRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReferenceId(String referenceId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Page<Payment> findByPayer_Id(Long payerId, Pageable pageable);

    Page<Payment> findByPayee_Id(Long payeeId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.payer.id = :userId OR p.payee.id = :userId")
    Page<Payment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.payer.id = :userId AND p.status = :status")
    Page<Payment> findByUserIdAndStatus(@Param("userId") Long userId, 
                                        @Param("status") Payment.PaymentStatus status, 
                                        Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.payer.id = :payerId AND p.payee.id = :payeeId")
    List<Payment> findByPayerAndPayee(@Param("payerId") Long payerId, 
                                      @Param("payeeId") Long payeeId);

    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") Payment.PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS'")
    Optional<java.math.BigDecimal> getTotalSuccessfulPayments();

    @Query("SELECT p FROM Payment p WHERE p.status IN ('PENDING', 'INITIATED') AND p.expiresAt < :now")
    List<Payment> findExpiredPendingPayments(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Payment p WHERE p.payer.id = :userId AND p.status = 'SUCCESS' ORDER BY p.createdAt DESC")
    List<Payment> findRecentSuccessfulPaymentsByUser(@Param("userId") Long userId, Pageable pageable);
}