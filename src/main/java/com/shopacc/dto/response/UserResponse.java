package com.shopacc.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role; // CUSTOMER, ADMIN, MANAGER, STAFF
    private String userType; // CUSTOMER or STAFF

    // Customer specific fields
    private BigDecimal balance;

    // ✅ BỎ isActive (không cần thiết cho response)
}