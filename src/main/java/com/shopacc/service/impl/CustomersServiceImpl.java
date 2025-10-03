package com.shopacc.service.impl;

import com.shopacc.model.Customers;
import com.shopacc.repository.CustomersRepository;
import com.shopacc.service.CustomersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomersServiceImpl implements CustomersService {

    private final CustomersRepository customersRepository;

    public CustomersServiceImpl(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    @Override
    public Customers save(Customers customer) {
        return customersRepository.save(customer);
    }

    @Override
    public Optional<Customers> findById(Integer id) {
        return customersRepository.findById(id);
    }

    @Override
    public List<Customers> findAll() {
        return customersRepository.findAll();
    }

    @Override
    public Optional<Customers> findByEmail(String email) {
        return customersRepository.findByEmail(email);
    }

    @Override
    public List<Customers> searchByName(String keyword) {
        return customersRepository.findByFullNameContainingIgnoreCase(keyword);
    }

    @Override
    public void deleteById(Integer id) {
        customersRepository.deleteById(id);
    }
}
