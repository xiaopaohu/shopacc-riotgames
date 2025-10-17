package com.shopacc.controller.admin;

import com.shopacc.security.SecurityUtils;
import com.shopacc.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    /**
     * Dashboard - All staff can access
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public String dashboard(Model model) {
        UserPrincipal user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        model.addAttribute("user", user);
        model.addAttribute("isAdmin", SecurityUtils.isAdmin());

        return "admin/dashboard";
    }

    /**
     * User Management - Admin & Manager only
     */
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String userManagement() {
        return "admin/users";
    }

    /**
     * Settings - Admin only
     */
    @GetMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public String settings() {
        return "admin/settings";
    }
}