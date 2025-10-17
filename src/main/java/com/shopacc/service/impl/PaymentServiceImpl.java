package com.shopacc.service.impl;

import com.shopacc.model.entity.Order;
import com.shopacc.model.entity.Payment;
import com.shopacc.repository.OrderRepository;
import com.shopacc.repository.PaymentRepository;
import com.shopacc.service.PaymentService;
import com.shopacc.service.base.SoftDeletableServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PaymentServiceImpl extends SoftDeletableServiceImpl<Payment, Integer> implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        super(paymentRepository);
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    protected String getEntityName() {
        return "Payment";
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findByOrder(Integer orderId) {
        log.debug("Finding payment by order: {}", orderId);
        return paymentRepository.findByOrderOrderIdAndDeletedAtIsNull(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding payments between {} and {}", startDate, endDate);
        return paymentRepository.findByPaymentDateBetweenAndDeletedAtIsNull(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Finding payments between {} and {} with pagination", startDate, endDate);
        return paymentRepository.findByPaymentDateBetweenAndDeletedAtIsNull(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Payment> searchByKeyword(String keyword, Pageable pageable) {
        log.debug("Searching payments with keyword: {}", keyword);
        return paymentRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentAmount() {
        BigDecimal total = paymentRepository.getTotalPaymentAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = paymentRepository.getTotalPaymentAmountByDateRange(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getRecentPayments(int limit) {
        log.debug("Getting {} recent payments", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return paymentRepository.findRecentPayments(pageable);
    }

    @Override
    public Payment createPaymentForOrder(Integer orderId) {
        log.info("Creating payment for order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Check if payment already exists
        if (findByOrder(orderId).isPresent()) {
            throw new IllegalStateException("Payment already exists for this order");
        }

        // Check order status
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new IllegalStateException("Can only create payment for completed orders");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getFinalAmount())
                .paymentDate(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllPayments() {
        return paymentRepository.countByDeletedAtIsNull();
    }

    @Override
    protected void beforeCreate(Payment entity) {
        log.info("Creating new payment for order: {}", entity.getOrder().getOrderId());

        if (entity.getOrder() == null || entity.getOrder().getOrderId() == null) {
            throw new IllegalArgumentException("Order is required");
        }

        if (entity.getAmount() == null || entity.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        if (entity.getPaymentDate() == null) {
            entity.setPaymentDate(LocalDateTime.now());
        }
    }
}