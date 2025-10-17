package com.shopacc.repository;

import com.shopacc.model.entity.Order;
import com.shopacc.repository.base.SoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends SoftDeletableRepository<Order, Integer> {

    List<Order> findByCustomerCustomerIdAndDeletedAtIsNull(Integer customerId);
    Page<Order> findByCustomerCustomerIdAndDeletedAtIsNull(Integer customerId, Pageable pageable);

    List<Order> findByStatusAndDeletedAtIsNull(String status);
    Page<Order> findByStatusAndDeletedAtIsNull(String status, Pageable pageable);

    List<Order> findByOrderDateBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

    long countByCustomerCustomerIdAndDeletedAtIsNull(Integer customerId);
    long countByStatusAndDeletedAtIsNull(String status);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.deletedAt IS NULL " +
            "AND o.customer.customerId = :customerId AND o.status = 'COMPLETED'")
    BigDecimal getTotalOrderValueByCustomer(@Param("customerId") Integer customerId);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.deletedAt IS NULL AND o.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.deletedAt IS NULL " +
            "AND o.status = 'COMPLETED' AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.customer c WHERE o.deletedAt IS NULL " +
            "AND (LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%'))")
    Page<Order> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}