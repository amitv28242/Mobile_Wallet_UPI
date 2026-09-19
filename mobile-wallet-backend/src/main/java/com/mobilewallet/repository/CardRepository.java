// FILE: src/main/java/com/mobilewallet/repository/CardRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByUser_Id(Long userId);

    Optional<Card> findByIdAndUser_Id(Long id, Long userId);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.isDefault = true")
    Optional<Card> findDefaultByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Card c SET c.isDefault = false WHERE c.user.id = :userId")
    int clearDefaultCards(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Card c SET c.isDefault = true WHERE c.id = :cardId AND c.user.id = :userId")
    int setDefaultCard(@Param("cardId") Long cardId, @Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.lastFour = :lastFour")
    List<Card> findByUserIdAndLastFour(@Param("userId") Long userId, @Param("lastFour") String lastFour);
}