package com.shopacc.service;

import com.shopacc.model.entity.PaymentMethod;
import com.shopacc.service.base.BaseService;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodService extends BaseService<PaymentMethod, Integer> {

    Optional<PaymentMethod> findByName(String methodName);

    List<PaymentMethod> findActiveMethods();

    boolean existsByName(String methodName);

    PaymentMethod toggleActive(Integer methodId, boolean active);

    long countActiveMethods();
}