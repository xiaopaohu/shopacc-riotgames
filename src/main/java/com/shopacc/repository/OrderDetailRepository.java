package com.shopacc.repository;

import com.shopacc.model.entity.OrderDetail;
import com.shopacc.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends BaseRepository<OrderDetail, Integer> {

    List<OrderDetail> findByOrderOrderId(Integer orderId);

    List<OrderDetail> findByAccountAccountId(Integer accountId);

    Optional<OrderDetail> findByOrderOrderIdAndAccountAccountId(Integer orderId, Integer accountId);

    long countByOrderOrderId(Integer orderId);

    @Query("SELECT COALESCE(SUM(od.price), 0) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    BigDecimal getTotalAmountByOrder(@Param("orderId") Integer orderId);
}