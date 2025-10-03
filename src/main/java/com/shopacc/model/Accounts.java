package com.shopacc.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ACCOUNTS")
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "game_type")
    private String gameType;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "level")
    private Integer level;

    @Column(name = "rank")
    private String rank;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "sold")
    private Boolean sold;

    @Column(name = "image")
    private String image;

    @Column(name = "listed_at")
    private LocalDateTime listedAt;
}


