package com.shopacc.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Security Utility Helper
 * Các phương thức tiện ích để làm việc với Security Context
 */
@Slf4j
@Component
public class SecurityUtils {

    /**
     * Lấy current authenticated user
     */
    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) principal);
        }

        return Optional.empty();
    }

    /**
     * Lấy ID của current user
     */
    public static Optional<Integer> getCurrentUserId() {
        return getCurrentUser().map(UserPrincipal::getId);
    }

    /**
     * Lấy email của current user
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(UserPrincipal::getEmail);
    }

    /**
     * Lấy role của current user
     */
    public static Optional<String> getCurrentUserRole() {
        return getCurrentUser().map(UserPrincipal::getRole);
    }

    /**
     * Kiểm tra user hiện tại là Staff
     */
    public static boolean isCurrentUserStaff() {
        return getCurrentUser()
                .map(UserPrincipal::isStaff)
                .orElse(false);
    }

    /**
     * Kiểm tra user hiện tại là Customer
     */
    public static boolean isCurrentUserCustomer() {
        return getCurrentUser()
                .map(UserPrincipal::isCustomer)
                .orElse(false);
    }

    /**
     * Kiểm tra user có role cụ thể
     */
    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(user -> user.hasRole(role))
                .orElse(false);
    }

    /**
     * Kiểm tra user có phải Admin/Manager
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("MANAGER");
    }

    /**
     * Kiểm tra user đã authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Log current user info
     */
    public static void logCurrentUser() {
        getCurrentUser().ifPresentOrElse(
                user -> log.info("Current user: {}", user),
                () -> log.info("No authenticated user")
        );
    }
}