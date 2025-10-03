package com.shopacc.model;

import jakarta.persistence.*;
import lombok.Data;
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

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Accounts account;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staffs staff;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    private String status;
}
