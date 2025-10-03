package com.shopacc.service;

import com.shopacc.model.Transactions;

import java.util.List;
import java.util.Optional;

public interface TransactionsService {
    Transactions save(Transactions transaction);
    Optional<Transactions> findById(Integer id);
    List<Transactions> findAll();
    List<Transactions> findByCustomer(Integer customerId);
    List<Transactions> findByCustomerAndAmountGreater(Integer customerId, Double amount);
    void deleteById(Integer id);
}
