package com.shopacc.service.impl;

import com.shopacc.model.entity.PaymentMethod;
import com.shopacc.repository.PaymentMethodRepository;
import com.shopacc.service.PaymentMethodService;
import com.shopacc.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PaymentMethodServiceImpl extends BaseServiceImpl<PaymentMethod, Integer> implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        super(paymentMethodRepository);
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    protected String getEntityName() {
        return "PaymentMethod";
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentMethod> findByName(String methodName) {
        return paymentMethodRepository.findByMethodName(methodName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethod> findActiveMethods() {
        return paymentMethodRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String methodName) {
        return paymentMethodRepository.existsByMethodName(methodName);
    }

    @Override
    public PaymentMethod toggleActive(Integer methodId, boolean active) {
        log.info("Toggle payment method {} active: {}", methodId, active);
        PaymentMethod method = getById(methodId);
        method.setIsActive(active);
        return paymentMethodRepository.save(method);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveMethods() {
        return paymentMethodRepository.countActivePaymentMethods();
    }

    @Override
    protected void beforeCreate(PaymentMethod entity) {
        if (existsByName(entity.getMethodName())) {
            throw new IllegalArgumentException("Payment method name already exists");
        }
        if (entity.getIsActive() == null) entity.setIsActive(true);
    }
}