package com.shopacc.repository;

import com.shopacc.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
    List<Payments> findByOrderOrderId(Integer orderId);
    List<Payments> findByPaymentDateBetween(Date start, Date end);
}
