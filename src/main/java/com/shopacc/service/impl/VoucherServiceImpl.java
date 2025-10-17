package com.shopacc.service.impl;

import com.shopacc.model.entity.Voucher;
import com.shopacc.repository.VoucherRepository;
import com.shopacc.service.VoucherService;
import com.shopacc.service.base.SoftDeletableServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class VoucherServiceImpl extends SoftDeletableServiceImpl<Voucher, Integer> implements VoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherServiceImpl(VoucherRepository voucherRepository) {
        super(voucherRepository);
        this.voucherRepository = voucherRepository;
    }

    @Override
    protected String getEntityName() {
        return "Voucher";
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Voucher> findByCode(String code) {
        return voucherRepository.findByCodeAndDeletedAtIsNull(code);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return voucherRepository.existsByCodeAndDeletedAtIsNull(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> findActiveVouchers() {
        return voucherRepository.findActiveVouchers(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Voucher> findActiveVouchers(Pageable pageable) {
        return voucherRepository.findActiveVouchers(LocalDateTime.now(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Voucher> searchByKeyword(String keyword, Pageable pageable) {
        return voucherRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidForOrder(String code, BigDecimal orderAmount) {
        Optional<Voucher> voucherOpt = findByCode(code);
        if (voucherOpt.isEmpty()) return false;

        Voucher voucher = voucherOpt.get();
        LocalDateTime now = LocalDateTime.now();

        return voucher.getIsActive() &&
                now.isAfter(voucher.getValidFrom()) && now.isBefore(voucher.getValidTo()) &&
                (voucher.getUsageLimit() == null || voucher.getUsedCount() < voucher.getUsageLimit()) &&
                orderAmount.compareTo(voucher.getMinOrderAmount()) >= 0;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(String code, BigDecimal orderAmount) {
        if (!isValidForOrder(code, orderAmount)) return BigDecimal.ZERO;

        Voucher voucher = findByCode(code).orElseThrow();
        BigDecimal discount;

        if ("PERCENT".equals(voucher.getDiscountType())) {
            discount = orderAmount.multiply(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                discount = voucher.getMaxDiscountAmount();
            }
        } else {
            discount = voucher.getDiscountValue();
        }

        return discount.min(orderAmount);
    }

    @Override
    public Voucher useVoucher(String code) {
        log.info("Using voucher: {}", code);
        Voucher voucher = findByCode(code).orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        return voucherRepository.save(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> findExpiringVouchers(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(days);
        return voucherRepository.findExpiringVouchers(now, endDate);
    }

    @Override
    public Voucher toggleActive(Integer voucherId, boolean active) {
        log.info("Toggle voucher {} active: {}", voucherId, active);
        Voucher voucher = getActiveById(voucherId);
        voucher.setIsActive(active);
        return voucherRepository.save(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveVouchers() {
        return voucherRepository.countActiveVouchers(LocalDateTime.now());
    }

    @Override
    protected void beforeCreate(Voucher entity) {
        if (existsByCode(entity.getCode())) {
            throw new IllegalArgumentException("Voucher code already exists");
        }
        if (entity.getValidTo().isBefore(entity.getValidFrom())) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (entity.getUsedCount() == null) entity.setUsedCount(0);
        if (entity.getIsActive() == null) entity.setIsActive(true);
    }
}