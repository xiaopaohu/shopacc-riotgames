package com.shopacc.repository;

import com.shopacc.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByCustomerId(Integer customerId);
    List<Orders> findByStatus(String status);
    List<Orders> findByCustomerIdAndStatus(Integer customerId, String status);
}
