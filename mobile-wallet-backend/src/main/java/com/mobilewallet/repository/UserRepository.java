// FILE: src/main/java/com/mobilewallet/repository/UserRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier OR u.phone = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") String identifier);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_CONSUMER'")
    List<User> findAllConsumers();

    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_MERCHANT'")
    List<User> findAllMerchants();

    @Query("SELECT u FROM User u WHERE u.enabled = :enabled")
    List<User> findByEnabled(@Param("enabled") boolean enabled);

    @Query("SELECT u FROM User u WHERE u.locked = :locked")
    List<User> findByLocked(@Param("locked") boolean locked);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_CONSUMER'")
    long countConsumers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_MERCHANT'")
    long countMerchants();
}