// FILE: src/main/java/com/mobilewallet/security/SecurityUtils.java
package com.mobilewallet.security;

import com.mobilewallet.entity.User;
import com.mobilewallet.exception.UnauthorizedAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }
        
        throw new UnauthorizedAccessException("Invalid principal type");
    }

    public Long getCurrentUserId() {
        return getCurrentUserPrincipal().getId();
    }

    public String getCurrentUsername() {
        return getCurrentUserPrincipal().getUsername();
    }

    public String getCurrentUserRole() {
        return getCurrentUserPrincipal().getRole();
    }

    public boolean hasRole(String role) {
        UserPrincipal principal = getCurrentUserPrincipal();
        return principal.getRole().equals(role);
    }

    public boolean isAdmin() {
        return hasRole(Role.ROLE_ADMIN.name());
    }

    public boolean isConsumer() {
        return hasRole(Role.ROLE_CONSUMER.name());
    }

    public boolean isMerchant() {
        return hasRole(Role.ROLE_MERCHANT.name());
    }

    public void validateUserAccess(Long userId) {
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(userId) && !isAdmin()) {
            throw new UnauthorizedAccessException("You don't have permission to access this resource");
        }
    }

    public void validateAdminAccess() {
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Admin access required");
        }
    }
}