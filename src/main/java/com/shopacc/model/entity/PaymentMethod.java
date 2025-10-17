package com.shopacc.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity cho bảng PAYMENT_METHODS (Phương thức thanh toán)
 */
@Entity
@Table(name = "PAYMENT_METHODS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private Integer methodId;

    @NotBlank(message = "Tên phương thức không được để trống")
    @Size(max = 50, message = "Tên phương thức không được vượt quá 50 ký tự")
    @Column(name = "method_name", nullable = false, length = 50)
    private String methodName;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "methodId=" + methodId +
                ", methodName='" + methodName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}