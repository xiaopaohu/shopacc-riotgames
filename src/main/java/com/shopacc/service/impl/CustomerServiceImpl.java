package com.shopacc.service.impl;

import com.shopacc.dto.request.SearchRequest;
import com.shopacc.dto.response.PageResponse;
import com.shopacc.model.entity.Customer;
import com.shopacc.repository.CustomerRepository;
import com.shopacc.service.CustomerService;
import com.shopacc.service.base.SoftDeletableServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CustomerServiceImpl extends SoftDeletableServiceImpl<Customer, Integer> implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        super(customerRepository);
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected String getEntityName() {
        return "Customer";
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByEmail(String email) {
        log.debug("Finding customer by email: {}", email);
        return customerRepository.findByEmailAndDeletedAtIsNull(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        log.debug("Finding customer by phone: {}", phoneNumber);
        return customerRepository.findByPhoneNumberAndDeletedAtIsNull(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Customer> search(SearchRequest request) {
        log.debug("Searching customers with keyword: {}", request.getKeyword());

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Customer> page = customerRepository.searchByKeyword(request.getKeyword(), pageable);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findByBalanceGreaterThanEqual(BigDecimal minBalance) {
        log.debug("Finding customers with balance >= {}", minBalance);
        return customerRepository.findByBalanceGreaterThanEqual(minBalance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findTopCustomersByBalance(int limit) {
        log.debug("Finding top {} customers by balance", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return customerRepository.findTopCustomersByBalance(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return customerRepository.existsByPhoneNumberAndDeletedAtIsNull(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveCustomersWithBalance() {
        return customerRepository.countActiveCustomersWithBalance();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCustomerBalance() {
        BigDecimal total = customerRepository.getTotalCustomerBalance();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Customer deposit(Integer customerId, BigDecimal amount) {
        log.info("Depositing {} to customer: {}", amount, customerId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0");
        }

        Customer customer = getActiveById(customerId);
        customer.setBalance(customer.getBalance().add(amount));

        return customerRepository.save(customer);
    }

    @Override
    public Customer withdraw(Integer customerId, BigDecimal amount) {
        log.info("Withdrawing {} from customer: {}", amount, customerId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than 0");
        }

        Customer customer = getActiveById(customerId);

        if (customer.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        customer.setBalance(customer.getBalance().subtract(amount));

        return customerRepository.save(customer);
    }

    @Override
    public Customer changePassword(Integer customerId, String oldPassword, String newPassword) {
        log.info("Changing password for customer: {}", customerId);

        Customer customer = getActiveById(customerId);

        // ⭐ SỬA: dùng passwordHash
        if (!passwordEncoder.matches(oldPassword, customer.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        customer.setPasswordHash(passwordEncoder.encode(newPassword));
        return customerRepository.save(customer);
    }

    @Override
    public Customer resetPassword(Integer customerId, String newPassword) {
        log.info("Resetting password for customer: {}", customerId);

        Customer customer = getActiveById(customerId);
        customer.setPasswordHash(passwordEncoder.encode(newPassword));
        return customerRepository.save(customer);
    }

    @Override
    protected void beforeCreate(Customer entity) {
        log.info("Creating new customer: {}", entity.getEmail());

        // Validate email uniqueness
        if (existsByEmail(entity.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + entity.getEmail());
        }

        // Validate phone uniqueness
        if (entity.getPhoneNumber() != null && existsByPhoneNumber(entity.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists: " + entity.getPhoneNumber());
        }

        // ⭐ SỬA: Encode passwordHash
        if (entity.getPasswordHash() != null && !entity.getPasswordHash().startsWith("$2a$")) {
            entity.setPasswordHash(passwordEncoder.encode(entity.getPasswordHash()));
        }

        // Initialize balance nếu null
        if (entity.getBalance() == null) {
            entity.setBalance(BigDecimal.ZERO);
        }
    }

    @Override
    protected void afterCreate(Customer entity) {
        log.info("Customer created successfully: {}", entity.getCustomerId());
    }

    @Override
    protected void beforeUpdate(Customer existingEntity, Customer newEntity) {
        log.info("Updating customer: {}", existingEntity.getCustomerId());

        // Validate email uniqueness nếu email thay đổi
        if (!existingEntity.getEmail().equals(newEntity.getEmail())) {
            if (existsByEmail(newEntity.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + newEntity.getEmail());
            }
        }

        // Validate phone uniqueness nếu phone thay đổi
        if (newEntity.getPhoneNumber() != null &&
                !newEntity.getPhoneNumber().equals(existingEntity.getPhoneNumber())) {
            if (existsByPhoneNumber(newEntity.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number already exists: " + newEntity.getPhoneNumber());
            }
        }

        // ⭐ THÊM: Giữ nguyên password cũ nếu không thay đổi
        if (newEntity.getPasswordHash() == null || newEntity.getPasswordHash().isEmpty()) {
            newEntity.setPasswordHash(existingEntity.getPasswordHash());
        } else if (!newEntity.getPasswordHash().startsWith("$2a$")) {
            newEntity.setPasswordHash(passwordEncoder.encode(newEntity.getPasswordHash()));
        }
    }

    @Override
    protected void beforeSoftDelete(Customer entity) {
        log.info("Soft deleting customer: {}", entity.getCustomerId());

        if (entity.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            log.warn("Customer {} has balance: {}", entity.getCustomerId(), entity.getBalance());
        }
    }
}