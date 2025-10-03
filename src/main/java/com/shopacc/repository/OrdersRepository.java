package com.shopacc.repository;

import com.shopacc.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByStatus(String status);
    List<Orders> findByCustomerCustomerId(Integer customerId);
    List<Orders> findByStaffStaffId(Integer staffId);
    List<Orders> findByOrderDateBetween(Date start, Date end);
}
