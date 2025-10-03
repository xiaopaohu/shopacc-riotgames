package com.shopacc.service;

import com.shopacc.model.Orders;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrdersService {
    Orders save(Orders order);
    Optional<Orders> findById(Integer id);
    List<Orders> findAll();
    List<Orders> findByStatus(String status);
    List<Orders> findByCustomerId(Integer customerId);
    List<Orders> findByStaffId(Integer staffId);
    List<Orders> findByOrderDateRange(Date start, Date end);
    void deleteById(Integer id);
}
