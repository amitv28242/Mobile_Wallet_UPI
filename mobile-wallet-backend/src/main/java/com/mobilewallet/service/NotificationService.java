// FILE: src/main/java/com/mobilewallet/service/NotificationService.java
package com.mobilewallet.service;

import com.mobilewallet.dto.notification.NotificationResponse;
import com.mobilewallet.entity.Notification;
import com.mobilewallet.entity.Payment;
import com.mobilewallet.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Notification createNotification(User user, String title, String message, String type, String data);

    void sendPaymentNotification(Payment payment);

    void sendOTPNotification(User user, String otp);

    void sendWalletNotification(User user, String title, String message, String data);

    void sendSecurityAlert(User user, String message, String data);

    Page<NotificationResponse> getNotifications(Long userId, Pageable pageable);

    Page<NotificationResponse> getUnreadNotifications(Long userId, Pageable pageable);

    long getUnreadCount(Long userId);

    void markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long userId, Long notificationId);

    void sendPushNotification(User user, String title, String message, String data);

    void sendPushNotificationToDevice(String deviceToken, String title, String message, String data);
}