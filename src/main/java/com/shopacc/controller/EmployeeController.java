package com.shopacc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

    @GetMapping("/employee/transactions")
    public String transactions() {
        return "employee/transactions";
    }
}

