package com.shopacc.service.impl;

import com.shopacc.model.entity.Customer;
import com.shopacc.model.entity.PaymentMethod;
import com.shopacc.model.entity.Transaction;
import com.shopacc.repository.CustomerRepository;
import com.shopacc.repository.PaymentMethodRepository;
import com.shopacc.repository.TransactionRepository;
import com.shopacc.service.TransactionService;
import com.shopacc.service.base.SoftDeletableServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class TransactionServiceImpl extends SoftDeletableServiceImpl<Transaction, Integer> implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  CustomerRepository customerRepository,
                                  PaymentMethodRepository paymentMethodRepository) {
        super(transactionRepository);
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    protected String getEntityName() {
        return "Transaction";
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByCustomer(Integer customerId) {
        return transactionRepository.findByCustomerCustomerIdAndDeletedAtIsNull(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByCustomer(Integer customerId, Pageable pageable) {
        return transactionRepository.findByCustomerCustomerIdAndDeletedAtIsNull(customerId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByStatus(String status) {
        return transactionRepository.findByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByStatus(String status, Pageable pageable) {
        return transactionRepository.findByStatusAndDeletedAtIsNull(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetweenAndDeletedAtIsNull(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return transactionRepository.findByTransactionDateBetweenAndDeletedAtIsNull(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> searchByKeyword(String keyword, Pageable pageable) {
        return transactionRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCustomer(Integer customerId) {
        return transactionRepository.countByCustomerCustomerIdAndDeletedAtIsNull(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return transactionRepository.countByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalTransactionAmountByCustomer(Integer customerId) {
        return transactionRepository.getTotalTransactionAmountByCustomer(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalTransactionAmount() {
        return transactionRepository.getTotalTransactionAmount();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalTransactionAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.getTotalTransactionAmountByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getRecentTransactions(int limit) {
        return transactionRepository.findRecentTransactions(PageRequest.of(0, limit));
    }

    @Override
    public Transaction createDeposit(Integer customerId, BigDecimal amount, Integer paymentMethodId, String note) {
        log.info("Creating deposit transaction for customer: {}, amount: {}", customerId, amount);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        Transaction transaction = Transaction.builder()
                .customer(customer)
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .note(note)
                .paymentMethod(paymentMethod)
                .status("PENDING")
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction createWithdrawal(Integer customerId, BigDecimal amount, String note) {
        log.info("Creating withdrawal transaction for customer: {}, amount: {}", customerId, amount);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (customer.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        Transaction transaction = Transaction.builder()
                .customer(customer)
                .amount(amount.negate())
                .transactionDate(LocalDateTime.now())
                .note(note)
                .status("PENDING")
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction approveTransaction(Integer transactionId) {
        log.info("Approving transaction: {}", transactionId);

        Transaction transaction = getActiveById(transactionId);

        if (!"PENDING".equals(transaction.getStatus())) {
            throw new IllegalStateException("Can only approve pending transactions");
        }

        transaction.setStatus("COMPLETED");

        // Update customer balance (trigger will handle this in DB, but we can also do it here)
        Customer customer = transaction.getCustomer();
        customer.setBalance(customer.getBalance().add(transaction.getAmount()));
        customerRepository.save(customer);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction rejectTransaction(Integer transactionId, String reason) {
        log.info("Rejecting transaction: {}, reason: {}", transactionId, reason);

        Transaction transaction = getActiveById(transactionId);

        if (!"PENDING".equals(transaction.getStatus())) {
            throw new IllegalStateException("Can only reject pending transactions");
        }

        transaction.setStatus("FAILED");
        transaction.setNote(transaction.getNote() + " | Rejected: " + reason);

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findByStatusAndDeletedAtIsNull("PENDING");
    }

    @Override
    protected void beforeCreate(Transaction entity) {
        if (entity.getTransactionDate() == null) {
            entity.setTransactionDate(LocalDateTime.now());
        }

        if (entity.getStatus() == null) {
            entity.setStatus("PENDING");
        }

        if (entity.getAmount() == null || entity.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Transaction amount must be non-zero");
        }
    }
}