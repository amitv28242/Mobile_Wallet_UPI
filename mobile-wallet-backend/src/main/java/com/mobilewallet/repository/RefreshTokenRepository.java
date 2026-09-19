// FILE: src/main/java/com/mobilewallet/repository/RefreshTokenRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT r FROM RefreshToken r WHERE r.user.id = :userId AND r.token = :token AND r.expiresAt > :now")
    Optional<RefreshToken> findValidToken(@Param("userId") Long userId,
                                          @Param("token") String token,
                                          @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
    int deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(r) FROM RefreshToken r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}