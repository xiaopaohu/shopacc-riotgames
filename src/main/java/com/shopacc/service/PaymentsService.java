package com.shopacc.service;

import com.shopacc.model.Payments;

import java.util.List;
import java.util.Optional;

public interface PaymentsService {
    Payments save(Payments payment);
    Optional<Payments> findById(Integer id);
    List<Payments> findAll();
    List<Payments> findByOrder(Integer orderId);
    void deleteById(Integer id);
}
