package com.shopacc.service;

import com.shopacc.model.Customers;

import java.util.List;
import java.util.Optional;

public interface CustomersService {
    Customers save(Customers customer);
    Optional<Customers> findById(Integer id);
    List<Customers> findAll();
    Optional<Customers> findByEmail(String email);
    List<Customers> searchByName(String keyword);
    void deleteById(Integer id);
}
