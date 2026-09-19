// FILE: src/main/java/com/mobilewallet/util/AuditUtil.java
package com.mobilewallet.util;

import com.mobilewallet.entity.AuditLog;
import com.mobilewallet.entity.User;
import com.mobilewallet.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditUtil {

    private final AuditLogRepository auditLogRepository;

    public void logAction(String action, User user, String details) {
        try {
            String ipAddress = getClientIpAddress();
            
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .user(user)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to log audit action: {}", action, e);
        }
    }

    public void logAdminAction(String action, User adminUser, User targetUser, String details) {
        try {
            String ipAddress = getClientIpAddress();
            
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .user(targetUser)
                    .admin(adminUser != null ? adminUser.getAdmin() : null)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to log admin audit action: {}", action, e);
        }
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty()) {
                    ipAddress = request.getRemoteAddr();
                }
                return ipAddress;
            }
        } catch (Exception e) {
            log.warn("Could not get client IP address", e);
        }
        return "UNKNOWN";
    }
}