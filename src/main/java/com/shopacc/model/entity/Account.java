package com.shopacc.model.entity;

import com.shopacc.model.entity.base.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity cho bảng ACCOUNTS (Tài khoản game)
 */
@Entity
@Table(name = "ACCOUNTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @NotBlank(message = "Loại game không được để trống")
    @Size(max = 50, message = "Loại game không được vượt quá 50 ký tự")
    @Column(name = "game_type", nullable = false, length = 50)
    private String gameType;

    @NotBlank(message = "Tên tài khoản không được để trống")
    @Size(max = 100, message = "Tên tài khoản không được vượt quá 100 ký tự")
    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @NotBlank(message = "Username không được để trống")
    @Size(max = 100, message = "Username không được vượt quá 100 ký tự")
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "level")
    private Integer level;

    @Size(max = 50, message = "Rank không được vượt quá 50 ký tự")
    @Column(name = "rank", length = 50)
    private String rank;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", message = "Giá không được âm")
    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Trạng thái không được để trống")
    @Size(max = 20, message = "Trạng thái không được vượt quá 20 ký tự")
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "AVAILABLE"; // AVAILABLE / SOLD / HIDDEN / DELIVERED

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "listed_at")
    private LocalDateTime listedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", referencedColumnName = "staff_id")
    private Staff staff;

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", gameType='" + gameType + '\'' +
                ", accountName='" + accountName + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}