package com.shopacc.repository;

import com.shopacc.model.entity.PaymentMethod;
import com.shopacc.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends BaseRepository<PaymentMethod, Integer> {

    Optional<PaymentMethod> findByMethodName(String methodName);

    List<PaymentMethod> findByIsActiveTrue();

    boolean existsByMethodName(String methodName);

    @Query("SELECT COUNT(pm) FROM PaymentMethod pm WHERE pm.isActive = true")
    long countActivePaymentMethods();
}