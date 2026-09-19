// FILE: src/main/java/com/mobilewallet/repository/RechargeTransactionRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.RechargeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RechargeTransactionRepository extends JpaRepository<RechargeTransaction, Long> {

    @Query("SELECT r FROM RechargeTransaction r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<RechargeTransaction> findByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<RechargeTransaction> findByReferenceId(String referenceId);

    @Query("SELECT r FROM RechargeTransaction r WHERE r.user.id = :userId AND r.operator = :operator ORDER BY r.createdAt DESC")
    Page<RechargeTransaction> findByUserIdAndOperator(@Param("userId") Long userId, 
                                                      @Param("operator") String operator, 
                                                      Pageable pageable);

    @Query("SELECT r FROM RechargeTransaction r WHERE r.phoneNumber = :phoneNumber ORDER BY r.createdAt DESC")
    Page<RechargeTransaction> findByPhoneNumber(@Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query("SELECT r FROM RechargeTransaction r WHERE r.status = 'PENDING' AND r.createdAt < :time")
    Page<RechargeTransaction> findPendingOlderThan(@Param("time") LocalDateTime time, Pageable pageable);

    @Query("SELECT COUNT(r) FROM RechargeTransaction r WHERE r.user.id = :userId AND r.status = 'SUCCESS'")
    long countSuccessfulByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(r.amount) FROM RechargeTransaction r WHERE r.user.id = :userId AND r.status = 'SUCCESS'")
    Optional<java.math.BigDecimal> sumAmountByUserId(@Param("userId") Long userId);
}