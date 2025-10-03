package com.shopacc.service;

import com.shopacc.model.Payments;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PaymentsService {
    Payments save(Payments payment);
    Optional<Payments> findById(Integer id);
    List<Payments> findAll();
    List<Payments> findByOrderId(Integer orderId);
    List<Payments> findByPaymentDateRange(Date start, Date end);
    void deleteById(Integer id);
}
