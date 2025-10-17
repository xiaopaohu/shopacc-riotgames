package com.shopacc.repository;

import com.shopacc.model.entity.OrderVoucher;
import com.shopacc.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderVoucherRepository extends BaseRepository<OrderVoucher, Integer> {

    List<OrderVoucher> findByOrderOrderId(Integer orderId);

    List<OrderVoucher> findByVoucherVoucherId(Integer voucherId);

    Optional<OrderVoucher> findByOrderOrderIdAndVoucherVoucherId(Integer orderId, Integer voucherId);

    boolean existsByOrderOrderIdAndVoucherVoucherId(Integer orderId, Integer voucherId);

    long countByVoucherVoucherId(Integer voucherId);

    @Query("SELECT COALESCE(SUM(ov.discountAmount), 0) FROM OrderVoucher ov WHERE ov.voucher.voucherId = :voucherId")
    BigDecimal getTotalDiscountByVoucher(@Param("voucherId") Integer voucherId);

    @Query("SELECT COALESCE(SUM(ov.discountAmount), 0) FROM OrderVoucher ov WHERE ov.order.orderId = :orderId")
    BigDecimal getTotalDiscountByOrder(@Param("orderId") Integer orderId);
}