package com.shopacc.service.impl;

import com.shopacc.model.Transactions;
import com.shopacc.repository.TransactionsRepository;
import com.shopacc.service.TransactionsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionsRepository transactionsRepository;

    public TransactionsServiceImpl(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    @Override
    public Transactions save(Transactions transaction) {
        return transactionsRepository.save(transaction);
    }

    @Override
    public Optional<Transactions> findById(Integer id) {
        return transactionsRepository.findById(id);
    }

    @Override
    public List<Transactions> findAll() {
        return transactionsRepository.findAll();
    }

    @Override
    public List<Transactions> findByCustomerId(Integer customerId) {
        return transactionsRepository.findByCustomerCustomerId(customerId);
    }

    @Override
    public List<Transactions> findByAmountAbove(BigDecimal min) {
        return transactionsRepository.findByAmountGreaterThanEqual(min);
    }

    @Override
    public List<Transactions> findByTransactionDateRange(Date start, Date end) {
        return transactionsRepository.findByTransactionDateBetween(start, end);
    }

    @Override
    public void deleteById(Integer id) {
        transactionsRepository.deleteById(id);
    }
}
