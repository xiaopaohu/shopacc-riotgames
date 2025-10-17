package com.shopacc.service;

import com.shopacc.model.entity.Notification;
import com.shopacc.service.base.SoftDeletableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService extends SoftDeletableService<Notification, Integer> {

    List<Notification> getByCustomer(Integer customerId);

    Page<Notification> getByCustomer(Integer customerId, Pageable pageable);

    List<Notification> getUnreadByCustomer(Integer customerId);

    List<Notification> getByStaff(Integer staffId);

    Page<Notification> getByStaff(Integer staffId, Pageable pageable);

    List<Notification> getUnreadByStaff(Integer staffId);

    List<Notification> getByType(String type);

    long countUnreadByCustomer(Integer customerId);

    long countUnreadByStaff(Integer staffId);

    void markAsRead(Integer notificationId);

    void markAllAsReadForCustomer(Integer customerId);

    void markAllAsReadForStaff(Integer staffId);

    Page<Notification> searchByKeyword(String keyword, Pageable pageable);

    Notification createForCustomer(Integer customerId, String title, String message, String type);

    Notification createForStaff(Integer staffId, String title, String message, String type);

    void createForAllCustomers(String title, String message, String type);

    void createForAllStaff(String title, String message, String type);

    void deleteOldReadNotifications(int daysOld);
}