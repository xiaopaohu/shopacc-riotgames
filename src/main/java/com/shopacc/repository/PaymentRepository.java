package com.shopacc.repository;

import com.shopacc.model.entity.Payment;
import com.shopacc.repository.base.SoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends SoftDeletableRepository<Payment, Integer> {

    Optional<Payment> findByOrderOrderIdAndDeletedAtIsNull(Integer orderId);

    List<Payment> findByPaymentDateBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

    Page<Payment> findByPaymentDateBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.deletedAt IS NULL")
    BigDecimal getTotalPaymentAmount();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.deletedAt IS NULL " +
            "AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalPaymentAmountByDateRange(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.deletedAt IS NULL ORDER BY p.paymentDate DESC")
    List<Payment> findRecentPayments(Pageable pageable);

    @Query("SELECT p FROM Payment p JOIN p.order o JOIN o.customer c WHERE p.deletedAt IS NULL " +
            "AND (LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR CAST(o.orderId AS string) LIKE CONCAT('%', :keyword, '%'))")
    Page<Payment> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    long countByDeletedAtIsNull();
}