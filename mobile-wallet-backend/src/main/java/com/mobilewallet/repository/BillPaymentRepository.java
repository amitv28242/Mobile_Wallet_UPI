// FILE: src/main/java/com/mobilewallet/repository/BillPaymentRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.BillPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {

    @Query("SELECT b FROM BillPayment b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    Page<BillPayment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<BillPayment> findByReferenceId(String referenceId);

    @Query("SELECT b FROM BillPayment b WHERE b.user.id = :userId AND b.billType = :billType ORDER BY b.createdAt DESC")
    Page<BillPayment> findByUserIdAndBillType(@Param("userId") Long userId, 
                                              @Param("billType") String billType, 
                                              Pageable pageable);

    @Query("SELECT b FROM BillPayment b WHERE b.consumerNumber = :consumerNumber ORDER BY b.createdAt DESC")
    Page<BillPayment> findByConsumerNumber(@Param("consumerNumber") String consumerNumber, Pageable pageable);

    @Query("SELECT b FROM BillPayment b WHERE b.status = 'PENDING' AND b.dueDate < :date")
    Page<BillPayment> findPendingPastDue(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT COUNT(b) FROM BillPayment b WHERE b.user.id = :userId AND b.status = 'SUCCESS'")
    long countSuccessfulByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(b.amount) FROM BillPayment b WHERE b.user.id = :userId AND b.status = 'SUCCESS'")
    Optional<java.math.BigDecimal> sumAmountByUserId(@Param("userId") Long userId);
}