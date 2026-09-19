// FILE: src/main/java/com/mobilewallet/repository/MerchantRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByUser_Id(Long userId);

    Optional<Merchant> findByBusinessName(String businessName);

    @Query("SELECT m FROM Merchant m WHERE m.verified = :verified")
    List<Merchant> findByVerified(@Param("verified") boolean verified);

    @Query("SELECT m FROM Merchant m WHERE m.gstNumber = :gstNumber")
    Optional<Merchant> findByGstNumber(@Param("gstNumber") String gstNumber);

    @Query("SELECT m FROM Merchant m WHERE m.businessPhone = :businessPhone")
    Optional<Merchant> findByBusinessPhone(@Param("businessPhone") String businessPhone);

    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.verified = true")
    long countVerifiedMerchants();

    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.verified = false")
    long countUnverifiedMerchants();
}