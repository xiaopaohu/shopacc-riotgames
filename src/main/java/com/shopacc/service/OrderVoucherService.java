package com.shopacc.service;

import com.shopacc.model.entity.OrderVoucher;
import com.shopacc.service.base.BaseService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderVoucherService extends BaseService<OrderVoucher, Integer> {

    List<OrderVoucher> getByOrder(Integer orderId);

    List<OrderVoucher> getByVoucher(Integer voucherId);

    Optional<OrderVoucher> getByOrderAndVoucher(Integer orderId, Integer voucherId);

    boolean isVoucherUsedInOrder(Integer orderId, Integer voucherId);

    long countVoucherUsage(Integer voucherId);

    BigDecimal getTotalDiscountByVoucher(Integer voucherId);

    BigDecimal getTotalDiscountByOrder(Integer orderId);

    OrderVoucher applyVoucherToOrder(Integer orderId, String voucherCode);

    void removeVoucherFromOrder(Integer orderId, Integer voucherId);
}