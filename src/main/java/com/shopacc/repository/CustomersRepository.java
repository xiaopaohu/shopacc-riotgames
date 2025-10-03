package com.shopacc.repository;

import com.shopacc.model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer> {
    Optional<Customers> findByEmail(String email);
    List<Customers> findByFullNameContainingIgnoreCase(String keyword);
    List<Customers> findByBalanceGreaterThanEqual(BigDecimal amount);
}
