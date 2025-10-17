package com.shopacc.repository;

import com.shopacc.model.entity.Cart;
import com.shopacc.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends BaseRepository<Cart, Integer> {

    List<Cart> findByCustomerCustomerId(Integer customerId);

    Optional<Cart> findByCustomerCustomerIdAndAccountAccountId(Integer customerId, Integer accountId);

    boolean existsByCustomerCustomerIdAndAccountAccountId(Integer customerId, Integer accountId);

    long countByCustomerCustomerId(Integer customerId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.customer.customerId = :customerId")
    void deleteByCustomerId(@Param("customerId") Integer customerId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.customer.customerId = :customerId AND c.account.accountId = :accountId")
    void deleteByCustomerIdAndAccountId(@Param("customerId") Integer customerId, @Param("accountId") Integer accountId);

    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Cart c JOIN c.account a WHERE c.customer.customerId = :customerId")
    BigDecimal getTotalCartValue(@Param("customerId") Integer customerId);
}