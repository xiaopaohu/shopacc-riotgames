package com.shopacc.service;

import com.shopacc.dto.request.LoginRequest;
import com.shopacc.dto.request.RegisterCustomerRequest;
import com.shopacc.dto.response.LoginResponse;
import com.shopacc.dto.response.UserResponse;
import com.shopacc.exception.CustomExceptions;
import com.shopacc.model.entity.Customer;
import com.shopacc.repository.CustomerRepository;
import com.shopacc.repository.StaffRepository;
import com.shopacc.security.UserPrincipal;
import com.shopacc.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;

    /**
     * Login - Xác thực user và trả về JWT token
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);

            // Get user info
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            UserResponse userResponse = convertToUserResponse(userPrincipal);

            log.info("Login successful for user: {} ({})", userPrincipal.getEmail(), userPrincipal.getUserType());

            return LoginResponse.builder()
                    .accessToken(jwt)
                    .tokenType("Bearer")
                    .user(userResponse)
                    .build();

        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            throw new CustomExceptions.InvalidCredentialsException("Email hoặc mật khẩu không đúng");
        }
    }

    /**
     * Register Customer
     */
    @Transactional
    public UserResponse registerCustomer(RegisterCustomerRequest request) {
        log.info("Registering new customer: {}", request.getEmail());

        // Check if email already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomExceptions.EmailAlreadyExistsException("Email đã được sử dụng");
        }

        if (staffRepository.existsByEmail(request.getEmail())) {
            throw new CustomExceptions.EmailAlreadyExistsException("Email đã được sử dụng");
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setBalance(BigDecimal.ZERO);
        customer.setCreatedAt(LocalDateTime.now());

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer registered successfully: {}", savedCustomer.getEmail());

        return UserResponse.builder()
                .id(savedCustomer.getCustomerId())
                .fullName(savedCustomer.getFullName())
                .email(savedCustomer.getEmail())
                .phoneNumber(savedCustomer.getPhoneNumber())
                .balance(savedCustomer.getBalance())
                .role("CUSTOMER")
                .userType("CUSTOMER")
                .build();
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomExceptions.UnauthorizedException("Bạn chưa đăng nhập");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return convertToUserResponse(userPrincipal);
    }

    /**
     * Logout - Clear security context
     */
    public void logout() {
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully");
    }

    /**
     * Convert UserPrincipal to UserResponse
     */
    private UserResponse convertToUserResponse(UserPrincipal userPrincipal) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(userPrincipal.getId())
                .fullName(userPrincipal.getFullName())
                .email(userPrincipal.getEmail())
                .role(userPrincipal.getRole())
                .userType(userPrincipal.getUserType().name());

        // Load additional info based on user type
        if (userPrincipal.isCustomer()) {
            customerRepository.findById(userPrincipal.getId()).ifPresent(customer -> {
                builder.phoneNumber(customer.getPhoneNumber())
                        .balance(customer.getBalance());
            });
        } else if (userPrincipal.isStaff()) {
            staffRepository.findById(userPrincipal.getId()).ifPresent(staff -> {
                builder.phoneNumber(staff.getPhoneNumber());
            });
        }

        return builder.build();
    }
}