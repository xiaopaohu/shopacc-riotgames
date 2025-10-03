package com.shopacc.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ORDERS")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    private String status;
}

