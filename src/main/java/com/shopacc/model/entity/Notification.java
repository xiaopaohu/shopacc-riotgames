package com.shopacc.model.entity;

import com.shopacc.model.entity.base.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity cho bảng NOTIFICATIONS (Thông báo)
 */
@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 500, message = "Nội dung không được vượt quá 500 ký tự")
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @NotBlank(message = "Loại thông báo không được để trống")
    @Size(max = 50, message = "Loại thông báo không được vượt quá 50 ký tự")
    @Column(name = "type", nullable = false, length = 50)
    private String type; // ORDER / PAYMENT / PROMOTION / SYSTEM

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    /**
     * Đánh dấu đã đọc
     */
    public void markAsRead() {
        this.isRead = true;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}