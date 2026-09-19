// FILE: src/main/java/com/mobilewallet/repository/AuditLogRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a ORDER BY a.createdAt DESC")
    Page<AuditLog> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.admin.id = :adminId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByAdminId(@Param("adminId") Long adminId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.action = :action ORDER BY a.createdAt DESC")
    Page<AuditLog> findByAction(@Param("action") String action, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate, 
                                   Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.action LIKE %:actionPattern% ORDER BY a.createdAt DESC")
    Page<AuditLog> findByActionContaining(@Param("actionPattern") String actionPattern, Pageable pageable);
}