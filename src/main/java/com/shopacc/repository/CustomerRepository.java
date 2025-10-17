package com.shopacc.repository;

import com.shopacc.model.entity.Customer;
import com.shopacc.repository.base.SoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends SoftDeletableRepository<Customer, Integer> {

    /**
     * Tìm customer theo email
     */
    Optional<Customer> findByEmailAndDeletedAtIsNull(String email);

    /**
     * Tìm customer theo số điện thoại
     */
    Optional<Customer> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber);

    /**
     * Lấy tất cả customers chưa bị xóa (THÊM METHOD NÀY)
     */
    List<Customer> findAllByDeletedAtIsNull();

    /**
     * Tìm kiếm customer theo keyword
     */
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL AND " +
            "(LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "c.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<Customer> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Tìm customer có số dư >= minBalance
     */
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL AND c.balance >= :minBalance")
    List<Customer> findByBalanceGreaterThanEqual(@Param("minBalance") BigDecimal minBalance);

    /**
     * Tìm top customers có số dư cao nhất
     */
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL ORDER BY c.balance DESC")
    List<Customer> findTopCustomersByBalance(Pageable pageable);

    /**
     * Kiểm tra email đã tồn tại
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * Kiểm tra phone đã tồn tại
     */
    boolean existsByPhoneNumberAndDeletedAtIsNull(String phoneNumber);

    /**
     * Đếm số customer có balance > 0
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.deletedAt IS NULL AND c.balance > 0")
    long countActiveCustomersWithBalance();

    /**
     * Tính tổng balance của tất cả customers
     */
    @Query("SELECT COALESCE(SUM(c.balance), 0) FROM Customer c WHERE c.deletedAt IS NULL")
    BigDecimal getTotalCustomerBalance();

    boolean existsByEmail(String email);
}