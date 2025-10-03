package com.shopacc.service;

import com.shopacc.model.Customers;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CustomersService {
    Customers save(Customers customer);
    Optional<Customers> findById(Integer id);
    Optional<Customers> findByEmail(String email);
    List<Customers> findByFullNameLike(String keyword);
    List<Customers> findByBalanceAbove(BigDecimal amount);
    List<Customers> findAll();
    void deleteById(Integer id);
}
