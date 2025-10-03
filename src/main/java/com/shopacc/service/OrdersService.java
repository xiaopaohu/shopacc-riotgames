package com.shopacc.service;

import com.shopacc.model.Orders;

import java.util.List;
import java.util.Optional;

public interface OrdersService {
    Orders save(Orders order);
    Optional<Orders> findById(Integer id);
    List<Orders> findAll();
    List<Orders> findByCustomer(Integer customerId);
    List<Orders> findByStatus(String status);
    void deleteById(Integer id);
}
