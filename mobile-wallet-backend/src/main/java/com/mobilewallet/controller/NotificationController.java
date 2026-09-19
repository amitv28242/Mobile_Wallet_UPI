// FILE: src/main/java/com/mobilewallet/controller/NotificationController.java
package com.mobilewallet.controller;

import com.mobilewallet.dto.notification.NotificationResponse;
import com.mobilewallet.dto.notification.NotificationStatusRequest;
import com.mobilewallet.entity.User;
import com.mobilewallet.service.AuthService;
import com.mobilewallet.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "Notification management APIs")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @Operation(summary = "Get all notifications")
    @GetMapping
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        
        User currentUser = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<NotificationResponse> notifications;
        if (unreadOnly) {
            notifications = notificationService.getUnreadNotifications(currentUser.getId(), pageable);
        } else {
            notifications = notificationService.getNotifications(currentUser.getId(), pageable);
        }
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get unread notification count")
    @GetMapping("/count/unread")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        User currentUser = authService.getCurrentUser();
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @Operation(summary = "Mark notification as read")
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long notificationId) {
        User currentUser = authService.getCurrentUser();
        notificationService.markAsRead(currentUser.getId(), notificationId);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }

    @Operation(summary = "Mark all notifications as read")
    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        User currentUser = authService.getCurrentUser();
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    @Operation(summary = "Delete notification")
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'MERCHANT')")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long notificationId) {
        User currentUser = authService.getCurrentUser();
        notificationService.deleteNotification(currentUser.getId(), notificationId);
        return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
    }
}