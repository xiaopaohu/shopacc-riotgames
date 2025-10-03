package com.shopacc.service.impl;

import com.shopacc.model.Payments;
import com.shopacc.repository.PaymentsRepository;
import com.shopacc.service.PaymentsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentsServiceImpl implements PaymentsService {

    private final PaymentsRepository paymentsRepository;

    public PaymentsServiceImpl(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    @Override
    public Payments save(Payments payment) {
        return paymentsRepository.save(payment);
    }

    @Override
    public Optional<Payments> findById(Integer id) {
        return paymentsRepository.findById(id);
    }

    @Override
    public List<Payments> findAll() {
        return paymentsRepository.findAll();
    }

    @Override
    public List<Payments> findByOrderId(Integer orderId) {
        return paymentsRepository.findByOrderOrderId(orderId);
    }

    @Override
    public List<Payments> findByPaymentDateRange(Date start, Date end) {
        return paymentsRepository.findByPaymentDateBetween(start, end);
    }

    @Override
    public void deleteById(Integer id) {
        paymentsRepository.deleteById(id);
    }
}
