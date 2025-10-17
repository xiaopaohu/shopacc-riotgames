package com.shopacc.service.impl;

import com.shopacc.model.entity.Customer;
import com.shopacc.model.entity.Notification;
import com.shopacc.model.entity.Staff;
import com.shopacc.repository.CustomerRepository;
import com.shopacc.repository.NotificationRepository;
import com.shopacc.repository.StaffRepository;
import com.shopacc.service.NotificationService;
import com.shopacc.service.base.SoftDeletableServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class NotificationServiceImpl extends SoftDeletableServiceImpl<Notification, Integer> implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   CustomerRepository customerRepository,
                                   StaffRepository staffRepository) {
        super(notificationRepository);
        this.notificationRepository = notificationRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
    }

    @Override
    protected String getEntityName() {
        return "Notification";
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getByCustomer(Integer customerId) {
        return notificationRepository.findByCustomerCustomerIdAndDeletedAtIsNullOrderByCreatedAtDesc(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getByCustomer(Integer customerId, Pageable pageable) {
        return notificationRepository.findByCustomerCustomerIdAndDeletedAtIsNullOrderByCreatedAtDesc(customerId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadByCustomer(Integer customerId) {
        return notificationRepository.findByCustomerCustomerIdAndIsReadFalseAndDeletedAtIsNullOrderByCreatedAtDesc(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getByStaff(Integer staffId) {
        return notificationRepository.findByStaffStaffIdAndDeletedAtIsNullOrderByCreatedAtDesc(staffId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getByStaff(Integer staffId, Pageable pageable) {
        return notificationRepository.findByStaffStaffIdAndDeletedAtIsNullOrderByCreatedAtDesc(staffId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadByStaff(Integer staffId) {
        return notificationRepository.findByStaffStaffIdAndIsReadFalseAndDeletedAtIsNullOrderByCreatedAtDesc(staffId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getByType(String type) {
        return notificationRepository.findByTypeAndDeletedAtIsNullOrderByCreatedAtDesc(type);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByCustomer(Integer customerId) {
        return notificationRepository.countByCustomerCustomerIdAndIsReadFalseAndDeletedAtIsNull(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByStaff(Integer staffId) {
        return notificationRepository.countByStaffStaffIdAndIsReadFalseAndDeletedAtIsNull(staffId);
    }

    @Override
    public void markAsRead(Integer notificationId) {
        log.info("Marking notification {} as read", notificationId);
        notificationRepository.markAsRead(notificationId);
    }

    @Override
    public void markAllAsReadForCustomer(Integer customerId) {
        log.info("Marking all notifications as read for customer: {}", customerId);
        notificationRepository.markAllAsReadForCustomer(customerId);
    }

    @Override
    public void markAllAsReadForStaff(Integer staffId) {
        log.info("Marking all notifications as read for staff: {}", staffId);
        notificationRepository.markAllAsReadForStaff(staffId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> searchByKeyword(String keyword, Pageable pageable) {
        return notificationRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    public Notification createForCustomer(Integer customerId, String title, String message, String type) {
        log.info("Creating notification for customer: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Notification notification = Notification.builder()
                .customer(customer)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    public Notification createForStaff(Integer staffId, String title, String message, String type) {
        log.info("Creating notification for staff: {}", staffId);

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        Notification notification = Notification.builder()
                .staff(staff)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    // ✅ THAY THẾ METHOD CŨ BẰNG CÁI NÀY (PHIÊN BẢN TỐI ƯU HƠN)
    @Override
    public void createForAllCustomers(String title, String message, String type) {
        log.info("Creating notification for all customers");

        List<Customer> customers = customerRepository.findAllByDeletedAtIsNull();

        // Tạo list notifications
        List<Notification> notifications = customers.stream()
                .map(customer -> Notification.builder()
                        .customer(customer)
                        .title(title)
                        .message(message)
                        .type(type)
                        .isRead(false)
                        .build())
                .toList();

        // Save all at once (faster than saving one by one)
        notificationRepository.saveAll(notifications);

        log.info("Created {} notifications", notifications.size());
    }

    // ✅ THAY THẾ METHOD CŨ BẰNG CÁI NÀY (PHIÊN BẢN TỐI ƯU HƠN)
    @Override
    public void createForAllStaff(String title, String message, String type) {
        log.info("Creating notification for all staff");

        List<Staff> staffList = staffRepository.findAllByDeletedAtIsNull();

        List<Notification> notifications = staffList.stream()
                .map(staff -> Notification.builder()
                        .staff(staff)
                        .title(title)
                        .message(message)
                        .type(type)
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);

        log.info("Created {} notifications", notifications.size());
    }

    @Override
    public void deleteOldReadNotifications(int daysOld) {
        log.info("Deleting read notifications older than {} days", daysOld);
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(daysOld);
        notificationRepository.deleteOldReadNotifications(beforeDate);
    }

    @Override
    protected void beforeCreate(Notification entity) {
        if (entity.getCustomer() == null && entity.getStaff() == null) {
            throw new IllegalArgumentException("Notification must have either customer or staff");
        }

        if (entity.getCustomer() != null && entity.getStaff() != null) {
            throw new IllegalArgumentException("Notification cannot have both customer and staff");
        }

        if (entity.getIsRead() == null) {
            entity.setIsRead(false);
        }
    }
}