// FILE: src/main/java/com/mobilewallet/repository/QRTransactionRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.QRTransaction;
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
public interface QRTransactionRepository extends JpaRepository<QRTransaction, Long> {

    Optional<QRTransaction> findByQrToken(String qrToken);

    Optional<QRTransaction> findByQrPayload(String qrPayload);

    @Query("SELECT q FROM QRTransaction q WHERE q.user.id = :userId AND q.status = 'ACTIVE'")
    List<QRTransaction> findActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT q FROM QRTransaction q WHERE q.status = 'ACTIVE' AND q.expiresAt < :now")
    List<QRTransaction> findExpiredActiveQRCodes(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE QRTransaction q SET q.status = 'EXPIRED' WHERE q.status = 'ACTIVE' AND q.expiresAt < :now")
    int expireExpiredQRCodes(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE QRTransaction q SET q.status = :newStatus WHERE q.qrToken = :qrToken")
    int updateStatusByQrToken(@Param("qrToken") String qrToken, 
                              @Param("newStatus") QRTransaction.QRStatus newStatus);

    @Query("SELECT COUNT(q) FROM QRTransaction q WHERE q.user.id = :userId AND q.status = 'ACTIVE'")
    long countActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT q FROM QRTransaction q WHERE q.qrToken = :qrToken AND q.status = 'ACTIVE' AND q.expiresAt > :now")
    Optional<QRTransaction> findValidActiveQR(@Param("qrToken") String qrToken, 
                                              @Param("now") LocalDateTime now);
}