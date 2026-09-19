// FILE: src/main/java/com/mobilewallet/service/impl/NotificationServiceImpl.java
package com.mobilewallet.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import com.mobilewallet.config.FirebaseConfig;
import com.mobilewallet.dto.notification.NotificationResponse;
import com.mobilewallet.entity.Notification;
import com.mobilewallet.entity.Payment;
import com.mobilewallet.entity.User;
import com.mobilewallet.exception.ResourceNotFoundException;
import com.mobilewallet.repository.NotificationRepository;
import com.mobilewallet.repository.UserRepository;
import com.mobilewallet.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FirebaseConfig firebaseConfig;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Notification createNotification(User user, String title, String message, String type, String data) {
        log.info("Creating notification for user: {}, type: {}", user.getId(), type);

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .data(data)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // Send push notification
        sendPushNotification(user, title, message, data);

        return savedNotification;
    }

    @Override
    @Async
    public void sendPaymentNotification(Payment payment) {
        log.info("Sending payment notification for payment: {}", payment.getReferenceId());

        // Notification for payer
        String payerMessage = "You have sent " + payment.getAmount() + 
                " to " + payment.getPayee().getFullName();
        createNotification(payment.getPayer(), "Payment Sent", payerMessage, "PAYMENT_SENT", 
                createPaymentData(payment));

        // Notification for payee
        String payeeMessage = "You have received " + payment.getAmount() + 
                " from " + payment.getPayer().getFullName();
        createNotification(payment.getPayee(), "Payment Received", payeeMessage, "PAYMENT_RECEIVED",
                createPaymentData(payment));
    }

    @Override
    @Async
    public void sendOTPNotification(User user, String otp) {
        log.info("Sending OTP notification for user: {}", user.getId());

        String message = "Your OTP for verification is: " + otp + " (Valid for 5 minutes)";
        createNotification(user, "OTP Verification", message, "OTP", 
                "{\"otp\":\"" + otp + "\"}");
    }

    @Override
    @Async
    public void sendWalletNotification(User user, String title, String message, String data) {
        log.info("Sending wallet notification for user: {}", user.getId());
        createNotification(user, title, message, "WALLET", data);
    }

    @Override
    @Async
    public void sendSecurityAlert(User user, String message, String data) {
        log.info("Sending security alert for user: {}", user.getId());
        createNotification(user, "Security Alert", message, "SECURITY", data);
    }

    @Override
    public Page<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        log.info("Fetching notifications for user: {}", userId);
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::convertToResponse);
    }

    @Override
    public Page<NotificationResponse> getUnreadNotifications(Long userId, Pageable pageable) {
        log.info("Fetching unread notifications for user: {}", userId);
        Page<Notification> notifications = notificationRepository.findUnreadByUserId(userId, pageable);
        return notifications.map(this::convertToResponse);
    }

    @Override
    public long getUnreadCount(Long userId) {
        log.info("Getting unread count for user: {}", userId);
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        log.info("Marking notification as read: {} for user: {}", notificationId, userId);
        int updated = notificationRepository.markAsRead(notificationId, userId);
        if (updated == 0) {
            throw new ResourceNotFoundException("Notification not found");
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        notificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        log.info("Deleting notification: {} for user: {}", notificationId, userId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new SecurityException("Cannot delete notification of another user");
        }
        
        notificationRepository.delete(notification);
    }

    @Override
    @Async
    public void sendPushNotification(User user, String title, String message, String data) {
        // TODO: Get FCM token from user device registration
        // For now, this is a placeholder
        log.info("Sending push notification to user: {}", user.getId());
        
        // In production, retrieve FCM token from user's device registration
        // String deviceToken = getDeviceToken(user.getId());
        // sendPushNotificationToDevice(deviceToken, title, message, data);
    }

    @Override
    public void sendPushNotificationToDevice(String deviceToken, String title, String message, String data) {
        if (deviceToken == null || deviceToken.isEmpty()) {
            log.warn("Device token is empty, skipping push notification");
            return;
        }

        if (firebaseConfig.firebaseMessaging() == null) {
            log.warn("Firebase is not configured, skipping push notification");
            return;
        }

        try {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("title", title);
            dataMap.put("message", message);
            dataMap.put("timestamp", LocalDateTime.now().toString());
            if (data != null) {
                dataMap.put("data", data);
            }

            Message fcmMessage = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .putAllData(dataMap)
                    .build();

            String response = firebaseConfig.firebaseMessaging().send(fcmMessage);
            log.info("Push notification sent: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification", e);
        }
    }

    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .data(notification.getData())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private String createPaymentData(Payment payment) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("paymentId", payment.getId());
            data.put("referenceId", payment.getReferenceId());
            data.put("amount", payment.getAmount());
            data.put("status", payment.getStatus().name());
            data.put("timestamp", payment.getCreatedAt().toString());
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("Failed to create payment data JSON", e);
            return "{}";
        }
    }
}