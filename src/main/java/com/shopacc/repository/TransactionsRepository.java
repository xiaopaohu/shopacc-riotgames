package com.shopacc.repository;

import com.shopacc.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {
    List<Transactions> findByCustomerId(Integer customerId);
    List<Transactions> findByCustomerIdAndAmountGreaterThan(Integer customerId, Double amount);
}
