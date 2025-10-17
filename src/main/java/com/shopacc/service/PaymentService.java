package com.shopacc.service;

import com.shopacc.model.entity.Payment;
import com.shopacc.service.base.SoftDeletableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentService extends SoftDeletableService<Payment, Integer> {

    Optional<Payment> findByOrder(Integer orderId);

    List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Page<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Payment> searchByKeyword(String keyword, Pageable pageable);

    BigDecimal getTotalPaymentAmount();

    BigDecimal getTotalPaymentAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> getRecentPayments(int limit);

    Payment createPaymentForOrder(Integer orderId);

    long countAllPayments();
}