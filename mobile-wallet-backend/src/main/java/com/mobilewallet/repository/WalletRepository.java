// FILE: src/main/java/com/mobilewallet/repository/WalletRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser_Id(Long userId);

    Optional<Wallet> findByWalletNumber(String walletNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdWithLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    Optional<Wallet> findByUserIdWithLock(@Param("userId") Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdWithOptimisticLock(@Param("id") Long id);

    boolean existsByUser_Id(Long userId);

    @Query("SELECT w FROM Wallet w WHERE w.status = 'ACTIVE'")
    Iterable<Wallet> findAllActive();

    @Query("SELECT SUM(w.balance) FROM Wallet w WHERE w.status = 'ACTIVE'")
    Optional<java.math.BigDecimal> getTotalActiveWalletBalance();
}