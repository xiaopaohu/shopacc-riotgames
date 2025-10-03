package com.shopacc.repository;

import com.shopacc.model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer> {
    Optional<Customers> findByEmail(String email);
    List<Customers> findByFullNameContainingIgnoreCase(String keyword);
}
