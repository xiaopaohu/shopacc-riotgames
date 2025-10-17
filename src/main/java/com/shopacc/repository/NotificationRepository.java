package com.shopacc.repository;

import com.shopacc.model.entity.Notification;
import com.shopacc.repository.base.SoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends SoftDeletableRepository<Notification, Integer> {

    List<Notification> findByCustomerCustomerIdAndDeletedAtIsNullOrderByCreatedAtDesc(Integer customerId);

    Page<Notification> findByCustomerCustomerIdAndDeletedAtIsNullOrderByCreatedAtDesc(Integer customerId, Pageable pageable);

    List<Notification> findByCustomerCustomerIdAndIsReadFalseAndDeletedAtIsNullOrderByCreatedAtDesc(Integer customerId);

    List<Notification> findByStaffStaffIdAndDeletedAtIsNullOrderByCreatedAtDesc(Integer staffId);

    Page<Notification> findByStaffStaffIdAndDeletedAtIsNullOrderByCreatedAtDesc(Integer staffId, Pageable pageable);

    List<Notification> findByStaffStaffIdAndIsReadFalseAndDeletedAtIsNullOrderByCreatedAtDesc(Integer staffId);

    List<Notification> findByTypeAndDeletedAtIsNullOrderByCreatedAtDesc(String type);

    long countByCustomerCustomerIdAndIsReadFalseAndDeletedAtIsNull(Integer customerId);

    long countByStaffStaffIdAndIsReadFalseAndDeletedAtIsNull(Integer staffId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId = :notificationId")
    void markAsRead(@Param("notificationId") Integer notificationId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.customer.customerId = :customerId AND n.deletedAt IS NULL")
    void markAllAsReadForCustomer(@Param("customerId") Integer customerId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.staff.staffId = :staffId AND n.deletedAt IS NULL")
    void markAllAsReadForStaff(@Param("staffId") Integer staffId);

    @Query("SELECT n FROM Notification n WHERE n.deletedAt IS NULL " +
            "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(n.message) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Notification> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.deletedAt = CURRENT_TIMESTAMP " +
            "WHERE n.isRead = true AND n.createdAt < :beforeDate AND n.deletedAt IS NULL")
    void deleteOldReadNotifications(@Param("beforeDate") LocalDateTime beforeDate);
}