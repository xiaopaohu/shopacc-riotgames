package com.shopacc.service.impl;

import com.shopacc.model.entity.Account;
import com.shopacc.model.entity.Order;
import com.shopacc.model.entity.OrderDetail;
import com.shopacc.repository.AccountRepository;
import com.shopacc.repository.OrderDetailRepository;
import com.shopacc.repository.OrderRepository;
import com.shopacc.service.OrderDetailService;
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
public class OrderDetailServiceImpl extends BaseServiceImpl<OrderDetail, Integer> implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository,
                                  OrderRepository orderRepository,
                                  AccountRepository accountRepository) {
        super(orderDetailRepository);
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    protected String getEntityName() {
        return "OrderDetail";
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetail> findByOrder(Integer orderId) {
        return orderDetailRepository.findByOrderOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetail> findByAccount(Integer accountId) {
        return orderDetailRepository.findByAccountAccountId(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDetail> findByOrderAndAccount(Integer orderId, Integer accountId) {
        return orderDetailRepository.findByOrderOrderIdAndAccountAccountId(orderId, accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByOrder(Integer orderId) {
        return orderDetailRepository.countByOrderOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByOrder(Integer orderId) {
        return orderDetailRepository.getTotalAmountByOrder(orderId);
    }

    @Override
    public OrderDetail addAccountToOrder(Integer orderId, Integer accountId, BigDecimal price) {
        log.info("Adding account {} to order {}", accountId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (!"AVAILABLE".equals(account.getStatus())) {
            throw new IllegalStateException("Account is not available");
        }

        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .account(account)
                .price(price)
                .build();

        OrderDetail saved = orderDetailRepository.save(orderDetail);

        // Update order total
        order.setTotalAmount(order.getTotalAmount().add(price));
        order.setFinalAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
        orderRepository.save(order);

        // Update account status
        account.setStatus("RESERVED");
        accountRepository.save(account);

        return saved;
    }

    @Override
    public void removeAccountFromOrder(Integer orderId, Integer accountId) {
        log.info("Removing account {} from order {}", accountId, orderId);

        OrderDetail orderDetail = findByOrderAndAccount(orderId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Order detail not found"));

        BigDecimal price = orderDetail.getPrice();

        orderDetailRepository.delete(orderDetail);

        Order order = orderDetail.getOrder();
        order.setTotalAmount(order.getTotalAmount().subtract(price));
        order.setFinalAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
        orderRepository.save(order);

        Account account = orderDetail.getAccount();
        account.setStatus("AVAILABLE");
        accountRepository.save(account);
    }
}