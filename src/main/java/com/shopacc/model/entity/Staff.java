package com.shopacc.model.entity;

import com.shopacc.model.entity.base.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity cho bảng STAFFS (Nhân viên)
 */
@Entity
@Table(name = "STAFFS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer staffId;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank(message = "Vai trò không được để trống")
    @Size(max = 50, message = "Vai trò không được vượt quá 50 ký tự")
    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "image", length = 255)
    private String image;

    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}