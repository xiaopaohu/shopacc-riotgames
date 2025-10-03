package com.shopacc.repository;

import com.shopacc.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {
    List<Transactions> findByCustomerCustomerId(Integer customerId);
    List<Transactions> findByAmountGreaterThanEqual(BigDecimal min);
    List<Transactions> findByTransactionDateBetween(Date start, Date end);
}
