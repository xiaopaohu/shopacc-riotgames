package com.shopacc.service;

import com.shopacc.model.entity.Cart;
import com.shopacc.service.base.BaseService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartService extends BaseService<Cart, Integer> {

    List<Cart> getCartByCustomer(Integer customerId);

    Cart addToCart(Integer customerId, Integer accountId);

    void removeFromCart(Integer customerId, Integer accountId);

    void clearCart(Integer customerId);

    boolean isInCart(Integer customerId, Integer accountId);

    long countCartItems(Integer customerId);

    BigDecimal getTotalCartValue(Integer customerId);

    Optional<Cart> getCartItem(Integer customerId, Integer accountId);
}