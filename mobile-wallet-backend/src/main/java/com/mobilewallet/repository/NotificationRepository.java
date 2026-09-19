// FILE: src/main/java/com/mobilewallet/repository/NotificationRepository.java
package com.mobilewallet.repository;

import com.mobilewallet.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser_Id(Long userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    Page<Notification> findUnreadByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.user.id = :userId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    int markAllAsRead(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdAndType(@Param("userId") Long userId, 
                                           @Param("type") String type, 
                                           Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.createdAt < :date")
    int deleteOldNotifications(@Param("date") java.time.LocalDateTime date);
}