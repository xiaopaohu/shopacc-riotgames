package com.shopacc.service;

import com.shopacc.model.entity.OrderDetail;
import com.shopacc.service.base.BaseService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderDetailService extends BaseService<OrderDetail, Integer> {

    List<OrderDetail> findByOrder(Integer orderId);

    List<OrderDetail> findByAccount(Integer accountId);

    Optional<OrderDetail> findByOrderAndAccount(Integer orderId, Integer accountId);

    long countByOrder(Integer orderId);

    BigDecimal getTotalAmountByOrder(Integer orderId);

    OrderDetail addAccountToOrder(Integer orderId, Integer accountId, BigDecimal price);

    void removeAccountFromOrder(Integer orderId, Integer accountId);
}