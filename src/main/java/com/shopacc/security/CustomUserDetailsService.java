package com.shopacc.security;

import com.shopacc.model.entity.Customer;
import com.shopacc.model.entity.Staff;
import com.shopacc.repository.CustomerRepository;
import com.shopacc.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Custom UserDetailsService
 * Hỗ trợ load cả Staff và Customer từ database
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);

        // Thử tìm Staff trước
        Optional<Staff> staffOpt = staffRepository.findByEmailAndDeletedAtIsNull(email);
        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            log.info("Staff found: {} with role: {}", email, staff.getRole());

            return UserPrincipal.createStaff(
                    staff.getStaffId(),
                    staff.getEmail(),
                    staff.getPasswordHash(), // ✅ ĐÚNG RỒI!
                    staff.getFullName(),
                    staff.getRole(),
                    staff.getDeletedAt() != null
            );
        }

        // Nếu không phải Staff, thử tìm Customer
        Optional<Customer> customerOpt = customerRepository.findByEmailAndDeletedAtIsNull(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            log.info("Customer found: {}", email);

            return UserPrincipal.createCustomer(
                    customer.getCustomerId(),
                    customer.getEmail(),
                    customer.getPasswordHash(), // ✅ ĐÚNG RỒI!
                    customer.getFullName(),
                    customer.getDeletedAt() != null
            );
        }

        // Không tìm thấy user nào
        log.error("User not found with email: {}", email);
        throw new UsernameNotFoundException("Email hoặc mật khẩu không đúng");
    }

    /**
     * Load Staff by email (specific)
     */
    @Transactional(readOnly = true)
    public UserPrincipal loadStaffByEmail(String email) {
        log.debug("Loading staff by email: {}", email);

        Staff staff = staffRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("Staff not found with email: " + email));

        return UserPrincipal.createStaff(
                staff.getStaffId(),
                staff.getEmail(),
                staff.getPasswordHash(),
                staff.getFullName(),
                staff.getRole(),
                staff.getDeletedAt() != null
        );
    }

    /**
     * Load Customer by email (specific)
     */
    @Transactional(readOnly = true)
    public UserPrincipal loadCustomerByEmail(String email) {
        log.debug("Loading customer by email: {}", email);

        Customer customer = customerRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));

        return UserPrincipal.createCustomer(
                customer.getCustomerId(),
                customer.getEmail(),
                customer.getPasswordHash(),
                customer.getFullName(),
                customer.getDeletedAt() != null
        );
    }

    /**
     * Load user by ID and type (for JWT)
     */
    @Transactional(readOnly = true)
    public UserPrincipal loadUserById(Integer id, String userType) {
        log.debug("Loading user by id: {} and type: {}", id, userType);

        if ("STAFF".equals(userType)) {
            Staff staff = staffRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Staff not found with id: " + id));

            return UserPrincipal.createStaff(
                    staff.getStaffId(),
                    staff.getEmail(),
                    staff.getPasswordHash(),
                    staff.getFullName(),
                    staff.getRole(),
                    staff.getDeletedAt() != null
            );
        } else if ("CUSTOMER".equals(userType)) {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Customer not found with id: " + id));

            return UserPrincipal.createCustomer(
                    customer.getCustomerId(),
                    customer.getEmail(),
                    customer.getPasswordHash(),
                    customer.getFullName(),
                    customer.getDeletedAt() != null
            );
        }

        throw new UsernameNotFoundException("Invalid user type: " + userType);
    }
}