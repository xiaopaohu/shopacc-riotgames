package com.shopacc.service;

import com.shopacc.model.Transactions;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionsService {
    Transactions save(Transactions transaction);
    Optional<Transactions> findById(Integer id);
    List<Transactions> findAll();
    List<Transactions> findByCustomerId(Integer customerId);
    List<Transactions> findByAmountAbove(BigDecimal min);
    List<Transactions> findByTransactionDateRange(Date start, Date end);
    void deleteById(Integer id);
}
