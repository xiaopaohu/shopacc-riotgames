package com.shopacc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerController {

    @GetMapping("/customer/home")
    public String home() {
        return "customer/home";
    }

    @GetMapping("/customer/accounts")
    public String accounts(@RequestParam(required = false) String game) {
        return "customer/accounts"; // có thể filter theo game
    }

    @GetMapping("/customer/wallet")
    public String wallet() {
        return "customer/wallet";
    }

    @GetMapping("/customer/profile")
    public String profile() {
        return "customer/profile";
    }
}
