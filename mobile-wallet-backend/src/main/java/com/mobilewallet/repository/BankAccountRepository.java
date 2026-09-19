// FILE: src/main/java/com/mobilewallet/repository/BankAccountRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findByUser_Id(Long userId);

    Optional<BankAccount> findByIdAndUser_Id(Long id, Long userId);

    @Query("SELECT b FROM BankAccount b WHERE b.user.id = :userId AND b.isDefault = true")
    Optional<BankAccount> findDefaultByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.isDefault = false WHERE b.user.id = :userId")
    int clearDefaultAccounts(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.isDefault = true WHERE b.id = :accountId AND b.user.id = :userId")
    int setDefaultAccount(@Param("accountId") Long accountId, @Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM BankAccount b WHERE b.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}