package com.shopacc.service.impl;

import com.shopacc.model.Orders;
import com.shopacc.repository.OrdersRepository;
import com.shopacc.service.OrdersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;

    public OrdersServiceImpl(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Override
    public Orders save(Orders order) {
        return ordersRepository.save(order);
    }

    @Override
    public Optional<Orders> findById(Integer id) {
        return ordersRepository.findById(id);
    }

    @Override
    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    @Override
    public List<Orders> findByCustomer(Integer customerId) {
        return ordersRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Orders> findByStatus(String status) {
        return ordersRepository.findByStatus(status);
    }

    @Override
    public void deleteById(Integer id) {
        ordersRepository.deleteById(id);
    }
}
