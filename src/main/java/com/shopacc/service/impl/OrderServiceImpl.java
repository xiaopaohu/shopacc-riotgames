package com.shopacc.service.impl;

import com.shopacc.model.entity.Customer;
import com.shopacc.model.entity.Order;
import com.shopacc.repository.CustomerRepository;
import com.shopacc.repository.OrderRepository;
import com.shopacc.service.OrderService;
import com.shopacc.service.base.SoftDeletableServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class OrderServiceImpl extends SoftDeletableServiceImpl<Order, Integer> implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected String getEntityName() {
        return "Order";
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCustomer(Integer customerId) {
        return orderRepository.findByCustomerCustomerIdAndDeletedAtIsNull(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByCustomer(Integer customerId, Pageable pageable) {
        return orderRepository.findByCustomerCustomerIdAndDeletedAtIsNull(customerId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatusAndDeletedAtIsNull(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetweenAndDeletedAtIsNull(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchByKeyword(String keyword, Pageable pageable) {
        return orderRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCustomer(Integer customerId) {
        return orderRepository.countByCustomerCustomerIdAndDeletedAtIsNull(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return orderRepository.countByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOrderValueByCustomer(Integer customerId) {
        return orderRepository.getTotalOrderValueByCustomer(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getRevenueByDateRange(startDate, endDate);
    }

    @Override
    public Order createOrderFromCart(Integer customerId) {
        log.info("Creating order from cart for customer: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Order order = Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .finalAmount(BigDecimal.ZERO)
                .build();

        return orderRepository.save(order);
    }

    @Override
    public Order updateStatus(Integer orderId, String status) {
        log.info("Updating order {} status to {}", orderId, status);
        Order order = getActiveById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Integer orderId, String reason) {
        log.info("Cancelling order {}: {}", orderId, reason);
        Order order = getActiveById(orderId);
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Can only cancel pending orders");
        }
        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }

    @Override
    public Order completeOrder(Integer orderId) {
        log.info("Completing order {}", orderId);
        Order order = getActiveById(orderId);
        order.setStatus("COMPLETED");
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findRecentOrders(PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatusAndDeletedAtIsNull("PENDING");
    }

    @Override
    protected void beforeCreate(Order entity) {
        if (entity.getOrderDate() == null) entity.setOrderDate(LocalDateTime.now());
        if (entity.getStatus() == null) entity.setStatus("PENDING");
        if (entity.getDiscountAmount() == null) entity.setDiscountAmount(BigDecimal.ZERO);
    }
}