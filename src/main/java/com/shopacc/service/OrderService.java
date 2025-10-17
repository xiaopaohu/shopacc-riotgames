package com.shopacc.service;

import com.shopacc.model.entity.Order;
import com.shopacc.service.base.SoftDeletableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService extends SoftDeletableService<Order, Integer> {

    List<Order> findByCustomer(Integer customerId);
    Page<Order> findByCustomer(Integer customerId, Pageable pageable);

    List<Order> findByStatus(String status);
    Page<Order> findByStatus(String status, Pageable pageable);

    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Page<Order> searchByKeyword(String keyword, Pageable pageable);

    long countByCustomer(Integer customerId);
    long countByStatus(String status);

    BigDecimal getTotalOrderValueByCustomer(Integer customerId);
    BigDecimal getTotalRevenue();
    BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Order createOrderFromCart(Integer customerId);
    Order updateStatus(Integer orderId, String status);
    Order cancelOrder(Integer orderId, String reason);
    Order completeOrder(Integer orderId);

    List<Order> getRecentOrders(int limit);
    List<Order> getPendingOrders();
}