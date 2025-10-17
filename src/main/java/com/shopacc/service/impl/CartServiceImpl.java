package com.shopacc.service.impl;

import com.shopacc.model.entity.Account;
import com.shopacc.model.entity.Cart;
import com.shopacc.model.entity.Customer;
import com.shopacc.repository.AccountRepository;
import com.shopacc.repository.CartRepository;
import com.shopacc.repository.CustomerRepository;
import com.shopacc.service.CartService;
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
public class CartServiceImpl extends BaseServiceImpl<Cart, Integer> implements CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CustomerRepository customerRepository,
                           AccountRepository accountRepository) {
        super(cartRepository);
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    protected String getEntityName() {
        return "Cart";
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> getCartByCustomer(Integer customerId) {
        return cartRepository.findByCustomerCustomerId(customerId);
    }

    @Override
    public Cart addToCart(Integer customerId, Integer accountId) {
        log.info("Adding account {} to cart for customer {}", accountId, customerId);

        if (isInCart(customerId, accountId)) {
            throw new IllegalArgumentException("Account already in cart");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (!"AVAILABLE".equals(account.getStatus())) {
            throw new IllegalStateException("Account is not available");
        }

        Cart cart = Cart.builder()
                .customer(customer)
                .account(account)
                .build();

        return cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(Integer customerId, Integer accountId) {
        log.info("Removing account {} from cart", accountId);
        cartRepository.deleteByCustomerIdAndAccountId(customerId, accountId);
    }

    @Override
    public void clearCart(Integer customerId) {
        log.info("Clearing cart for customer {}", customerId);
        cartRepository.deleteByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInCart(Integer customerId, Integer accountId) {
        return cartRepository.existsByCustomerCustomerIdAndAccountAccountId(customerId, accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCartItems(Integer customerId) {
        return cartRepository.countByCustomerCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCartValue(Integer customerId) {
        BigDecimal total = cartRepository.getTotalCartValue(customerId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> getCartItem(Integer customerId, Integer accountId) {
        return cartRepository.findByCustomerCustomerIdAndAccountAccountId(customerId, accountId);
    }
}