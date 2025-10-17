package com.shopacc.controller.api;

import com.shopacc.dto.request.LoginRequest;
import com.shopacc.dto.request.RegisterCustomerRequest;
import com.shopacc.dto.response.ApiResponse;
import com.shopacc.dto.response.LoginResponse;
import com.shopacc.dto.response.UserResponse;
import com.shopacc.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Đăng nhập và nhận JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Đăng nhập thành công", response)
        );
    }

    /**
     * POST /api/auth/register
     * Đăng ký tài khoản Customer mới
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterCustomerRequest request) {
        log.info("Register request received for email: {}", request.getEmail());

        UserResponse response = authService.registerCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký thành công", response));
    }

    /**
     * GET /api/auth/me
     * Lấy thông tin user hiện tại
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        log.info("Get current user request");

        UserResponse response = authService.getCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    /**
     * POST /api/auth/logout
     * Đăng xuất
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        log.info("Logout request");

        authService.logout();

        return ResponseEntity.ok(
                ApiResponse.success("Đăng xuất thành công")
        );
    }
}