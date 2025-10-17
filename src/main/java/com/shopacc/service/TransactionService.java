package com.shopacc.service;

import com.shopacc.model.entity.Transaction;
import com.shopacc.service.base.SoftDeletableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService extends SoftDeletableService<Transaction, Integer> {

    List<Transaction> findByCustomer(Integer customerId);

    Page<Transaction> findByCustomer(Integer customerId, Pageable pageable);

    List<Transaction> findByStatus(String status);

    Page<Transaction> findByStatus(String status, Pageable pageable);

    List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Page<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Transaction> searchByKeyword(String keyword, Pageable pageable);

    long countByCustomer(Integer customerId);

    long countByStatus(String status);

    BigDecimal getTotalTransactionAmountByCustomer(Integer customerId);

    BigDecimal getTotalTransactionAmount();

    BigDecimal getTotalTransactionAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> getRecentTransactions(int limit);

    Transaction createDeposit(Integer customerId, BigDecimal amount, Integer paymentMethodId, String note);

    Transaction createWithdrawal(Integer customerId, BigDecimal amount, String note);

    Transaction approveTransaction(Integer transactionId);

    Transaction rejectTransaction(Integer transactionId, String reason);

    List<Transaction> getPendingTransactions();
}