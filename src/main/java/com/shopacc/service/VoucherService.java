package com.shopacc.service;

import com.shopacc.model.entity.Voucher;
import com.shopacc.service.base.SoftDeletableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VoucherService extends SoftDeletableService<Voucher, Integer> {

    Optional<Voucher> findByCode(String code);

    boolean existsByCode(String code);

    List<Voucher> findActiveVouchers();

    Page<Voucher> findActiveVouchers(Pageable pageable);

    Page<Voucher> searchByKeyword(String keyword, Pageable pageable);

    boolean isValidForOrder(String code, BigDecimal orderAmount);

    BigDecimal calculateDiscount(String code, BigDecimal orderAmount);

    Voucher useVoucher(String code);

    List<Voucher> findExpiringVouchers(int days);

    Voucher toggleActive(Integer voucherId, boolean active);

    long countActiveVouchers();
}