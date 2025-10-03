package com.shopacc.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PAYMENTS")
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "order_id")
    private Integer orderId; // FK tới ORDERS

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
}

