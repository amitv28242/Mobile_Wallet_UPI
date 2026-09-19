// FILE: src/main/java/com/mobilewallet/repository/AdminRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUser_Id(Long userId);

    @Query("SELECT a FROM Admin a WHERE a.adminLevel = 'SUPER_ADMIN'")
    Optional<Admin> findSuperAdmin();

    @Query("SELECT a FROM Admin a WHERE a.user.username = :username")
    Optional<Admin> findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(a) FROM Admin a WHERE a.adminLevel = :level")
    long countByAdminLevel(@Param("level") Admin.AdminLevel level);
}