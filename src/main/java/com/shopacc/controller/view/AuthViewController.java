package com.shopacc.controller.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AuthViewController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        log.info("Accessing login page");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        log.info("Accessing register page");
        return "auth/register";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard() {
        log.info("Accessing customer dashboard");
        return "customer/dashboard";
    }
}