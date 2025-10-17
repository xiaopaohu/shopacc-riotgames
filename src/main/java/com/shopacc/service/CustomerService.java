package com.shopacc.service;

import com.shopacc.dto.request.SearchRequest;
import com.shopacc.dto.response.PageResponse;
import com.shopacc.model.entity.Customer;
import com.shopacc.service.base.SoftDeletableService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CustomerService extends SoftDeletableService<Customer, Integer> {

    /**
     * Tìm customer theo email
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Tìm customer theo số điện thoại
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Tìm kiếm customer
     */
    PageResponse<Customer> search(SearchRequest request);

    /**
     * Tìm customers có balance >= minBalance
     */
    List<Customer> findByBalanceGreaterThanEqual(BigDecimal minBalance);

    /**
     * Tìm top customers theo balance
     */
    List<Customer> findTopCustomersByBalance(int limit);

    /**
     * Kiểm tra email đã tồn tại
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra phone đã tồn tại
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Đếm customers có balance > 0
     */
    long countActiveCustomersWithBalance();

    /**
     * Tính tổng balance
     */
    BigDecimal getTotalCustomerBalance();

    /**
     * Nạp tiền vào tài khoản
     */
    Customer deposit(Integer customerId, BigDecimal amount);

    /**
     * Trừ tiền từ tài khoản
     */
    Customer withdraw(Integer customerId, BigDecimal amount);

    /**
     * Thay đổi password
     */
    Customer changePassword(Integer customerId, String oldPassword, String newPassword);

    /**
     * Reset password
     */
    Customer resetPassword(Integer customerId, String newPassword);
}