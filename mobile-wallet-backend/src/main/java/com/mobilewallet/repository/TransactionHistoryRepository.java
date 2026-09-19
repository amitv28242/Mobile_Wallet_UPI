// FILE: src/main/java/com/mobilewallet/repository/TransactionHistoryRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.TransactionHistory;
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
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    Page<TransactionHistory> findByUser_Id(Long userId, Pageable pageable);

    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    Page<TransactionHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM TransactionHistory t WHERE t.wallet.id = :walletId ORDER BY t.createdAt DESC")
    Page<TransactionHistory> findByWalletId(@Param("walletId") Long walletId, Pageable pageable);

    @Query("SELECT t FROM TransactionHistory t WHERE t.payment.id = :paymentId")
    List<TransactionHistory> findByPaymentId(@Param("paymentId") Long paymentId);

    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId AND t.transactionType = 'DEBIT' ORDER BY t.createdAt DESC")
    Page<TransactionHistory> findDebitsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId AND t.transactionType = 'CREDIT' ORDER BY t.createdAt DESC")
    Page<TransactionHistory> findCreditsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId AND t.reference = :reference")
    Optional<TransactionHistory> findByUserIdAndReference(@Param("userId") Long userId, @Param("reference") String reference);

    @Query("SELECT t FROM TransactionHistory t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    Page<TransactionHistory> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate, 
                                             Pageable pageable);

    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    Page<TransactionHistory> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                      @Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate, 
                                                      Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM TransactionHistory t WHERE t.user.id = :userId AND t.transactionType = 'DEBIT' AND t.status = 'SUCCESS'")
    Optional<java.math.BigDecimal> sumDebitsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(t.amount) FROM TransactionHistory t WHERE t.user.id = :userId AND t.transactionType = 'CREDIT' AND t.status = 'SUCCESS'")
    Optional<java.math.BigDecimal> sumCreditsByUserId(@Param("userId") Long userId);
}