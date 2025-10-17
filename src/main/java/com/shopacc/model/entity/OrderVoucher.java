package com.shopacc.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity cho bảng ORDER_VOUCHERS (Voucher đã sử dụng)
 */
@Entity
@Table(name = "ORDER_VOUCHERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_voucher_id")
    private Integer orderVoucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @NotNull(message = "Số tiền giảm không được để trống")
    @DecimalMin(value = "0.0", message = "Số tiền giảm không được âm")
    @Column(name = "discount_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Override
    public String toString() {
        return "OrderVoucher{" +
                "orderVoucherId=" + orderVoucherId +
                ", orderId=" + (order != null ? order.getOrderId() : null) +
                ", voucherId=" + (voucher != null ? voucher.getVoucherId() : null) +
                ", discountAmount=" + discountAmount +
                '}';
    }
}