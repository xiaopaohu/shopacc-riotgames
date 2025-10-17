package com.shopacc.repository;

import com.shopacc.model.entity.Transaction;
import com.shopacc.repository.base.SoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends SoftDeletableRepository<Transaction, Integer> {

    List<Transaction> findByCustomerCustomerIdAndDeletedAtIsNull(Integer customerId);

    Page<Transaction> findByCustomerCustomerIdAndDeletedAtIsNull(Integer customerId, Pageable pageable);

    List<Transaction> findByStatusAndDeletedAtIsNull(String status);

    Page<Transaction> findByStatusAndDeletedAtIsNull(String status, Pageable pageable);

    List<Transaction> findByTransactionDateBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

    Page<Transaction> findByTransactionDateBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    long countByCustomerCustomerIdAndDeletedAtIsNull(Integer customerId);

    long countByStatusAndDeletedAtIsNull(String status);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.deletedAt IS NULL " +
            "AND t.customer.customerId = :customerId AND t.status = 'COMPLETED'")
    BigDecimal getTotalTransactionAmountByCustomer(@Param("customerId") Integer customerId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.deletedAt IS NULL " +
            "AND t.status = 'COMPLETED'")
    BigDecimal getTotalTransactionAmount();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.deletedAt IS NULL " +
            "AND t.status = 'COMPLETED' AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalTransactionAmountByDateRange(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE t.deletedAt IS NULL ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactions(Pageable pageable);

    @Query("SELECT t FROM Transaction t JOIN t.customer c WHERE t.deletedAt IS NULL " +
            "AND (LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.note) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Transaction> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}