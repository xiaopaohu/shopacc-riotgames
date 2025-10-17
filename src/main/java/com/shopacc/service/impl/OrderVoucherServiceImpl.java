package com.shopacc.service.impl;

import com.shopacc.model.entity.Order;
import com.shopacc.model.entity.OrderVoucher;
import com.shopacc.model.entity.Voucher;
import com.shopacc.repository.OrderRepository;
import com.shopacc.repository.OrderVoucherRepository;
import com.shopacc.repository.VoucherRepository;
import com.shopacc.service.OrderVoucherService;
import com.shopacc.service.VoucherService;
import com.shopacc.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class OrderVoucherServiceImpl extends BaseServiceImpl<OrderVoucher, Integer> implements OrderVoucherService {

    private final OrderVoucherRepository orderVoucherRepository;
    private final OrderRepository orderRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherService voucherService;

    public OrderVoucherServiceImpl(OrderVoucherRepository orderVoucherRepository,
                                   OrderRepository orderRepository,
                                   VoucherRepository voucherRepository,
                                   VoucherService voucherService) {
        super(orderVoucherRepository);
        this.orderVoucherRepository = orderVoucherRepository;
        this.orderRepository = orderRepository;
        this.voucherRepository = voucherRepository;
        this.voucherService = voucherService;
    }

    @Override
    protected String getEntityName() {
        return "OrderVoucher";
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderVoucher> getByOrder(Integer orderId) {
        return orderVoucherRepository.findByOrderOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderVoucher> getByVoucher(Integer voucherId) {
        return orderVoucherRepository.findByVoucherVoucherId(voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderVoucher> getByOrderAndVoucher(Integer orderId, Integer voucherId) {
        return orderVoucherRepository.findByOrderOrderIdAndVoucherVoucherId(orderId, voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVoucherUsedInOrder(Integer orderId, Integer voucherId) {
        return orderVoucherRepository.existsByOrderOrderIdAndVoucherVoucherId(orderId, voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVoucherUsage(Integer voucherId) {
        return orderVoucherRepository.countByVoucherVoucherId(voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDiscountByVoucher(Integer voucherId) {
        return orderVoucherRepository.getTotalDiscountByVoucher(voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDiscountByOrder(Integer orderId) {
        return orderVoucherRepository.getTotalDiscountByOrder(orderId);
    }

    @Override
    public OrderVoucher applyVoucherToOrder(Integer orderId, String voucherCode) {
        log.info("Applying voucher {} to order {}", voucherCode, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Can only apply voucher to pending orders");
        }

        Voucher voucher = voucherRepository.findByCodeAndDeletedAtIsNull(voucherCode)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));

        if (isVoucherUsedInOrder(orderId, voucher.getVoucherId())) {
            throw new IllegalStateException("Voucher already applied");
        }

        if (!voucherService.isValidForOrder(voucherCode, order.getTotalAmount())) {
            throw new IllegalStateException("Voucher is not valid");
        }

        BigDecimal discountAmount = voucherService.calculateDiscount(voucherCode, order.getTotalAmount());

        OrderVoucher orderVoucher = OrderVoucher.builder()
                .order(order)
                .voucher(voucher)
                .discountAmount(discountAmount)
                .build();

        OrderVoucher saved = orderVoucherRepository.save(orderVoucher);

        order.setDiscountAmount(order.getDiscountAmount().add(discountAmount));
        order.setFinalAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
        orderRepository.save(order);

        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);

        return saved;
    }

    @Override
    public void removeVoucherFromOrder(Integer orderId, Integer voucherId) {
        log.info("Removing voucher {} from order {}", voucherId, orderId);

        OrderVoucher orderVoucher = getByOrderAndVoucher(orderId, voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Order voucher not found"));

        Order order = orderVoucher.getOrder();

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Can only remove voucher from pending orders");
        }

        BigDecimal discountAmount = orderVoucher.getDiscountAmount();

        orderVoucherRepository.delete(orderVoucher);

        order.setDiscountAmount(order.getDiscountAmount().subtract(discountAmount));
        order.setFinalAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
        orderRepository.save(order);

        Voucher voucher = orderVoucher.getVoucher();
        if (voucher.getUsedCount() > 0) {
            voucher.setUsedCount(voucher.getUsedCount() - 1);
            voucherRepository.save(voucher);
        }
    }
}