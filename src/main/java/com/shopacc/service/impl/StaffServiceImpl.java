package com.shopacc.service.impl;

import com.shopacc.dto.request.SearchRequest;
import com.shopacc.dto.response.PageResponse;
import com.shopacc.model.entity.Staff;
import com.shopacc.repository.StaffRepository;
import com.shopacc.service.StaffService;
import com.shopacc.service.base.SoftDeletableServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class StaffServiceImpl extends SoftDeletableServiceImpl<Staff, Integer> implements StaffService {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffServiceImpl(StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        super(staffRepository);
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected String getEntityName() {
        return "Staff";
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> findByEmail(String email) {
        log.debug("Finding staff by email: {}", email);
        return staffRepository.findByEmailAndDeletedAtIsNull(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> findByRole(String role) {
        log.debug("Finding staff by role: {}", role);
        return staffRepository.findByRoleAndDeletedAtIsNull(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Staff> findByRole(String role, Pageable pageable) {
        log.debug("Finding staff by role: {} with pagination", role);
        return staffRepository.findByRoleAndDeletedAtIsNull(role, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Staff> search(SearchRequest request) {
        log.debug("Searching staff with keyword: {}", request.getKeyword());

        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Staff> page = staffRepository.searchByKeyword(request.getKeyword(), pageable);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return staffRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRole(String role) {
        return staffRepository.countByRoleAndDeletedAtIsNull(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> findByRoles(List<String> roles) {
        log.debug("Finding staff by roles: {}", roles);
        return staffRepository.findByRolesIn(roles);
    }

    @Override
    public Staff changePassword(Integer staffId, String oldPassword, String newPassword) {
        log.info("Changing password for staff: {}", staffId);

        Staff staff = getActiveById(staffId);

        // ⭐ SỬA: dùng passwordHash thay vì password
        if (!passwordEncoder.matches(oldPassword, staff.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        staff.setPasswordHash(passwordEncoder.encode(newPassword));
        return staffRepository.save(staff);
    }

    @Override
    public Staff resetPassword(Integer staffId, String newPassword) {
        log.info("Resetting password for staff: {}", staffId);

        Staff staff = getActiveById(staffId);
        staff.setPasswordHash(passwordEncoder.encode(newPassword));
        return staffRepository.save(staff);
    }

    @Override
    public Staff updateRole(Integer staffId, String newRole) {
        log.info("Updating role for staff: {} to {}", staffId, newRole);

        Staff staff = getActiveById(staffId);
        staff.setRole(newRole);
        return staffRepository.save(staff);
    }

    @Override
    protected void beforeCreate(Staff entity) {
        log.info("Creating new staff: {}", entity.getEmail());

        // Validate email uniqueness
        if (existsByEmail(entity.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + entity.getEmail());
        }

        // ⭐ SỬA: Encode passwordHash
        if (entity.getPasswordHash() != null && !entity.getPasswordHash().startsWith("$2a$")) {
            // Chỉ encode nếu chưa được encode (không bắt đầu bằng $2a$ - BCrypt prefix)
            entity.setPasswordHash(passwordEncoder.encode(entity.getPasswordHash()));
        }
    }

    @Override
    protected void afterCreate(Staff entity) {
        log.info("Staff created successfully: {}", entity.getStaffId());
    }

    @Override
    protected void beforeUpdate(Staff existingEntity, Staff newEntity) {
        log.info("Updating staff: {}", existingEntity.getStaffId());

        // Validate email uniqueness nếu email thay đổi
        if (!existingEntity.getEmail().equals(newEntity.getEmail())) {
            if (existsByEmail(newEntity.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + newEntity.getEmail());
            }
        }

        // ⭐ THÊM: Giữ nguyên password cũ nếu không thay đổi
        if (newEntity.getPasswordHash() == null || newEntity.getPasswordHash().isEmpty()) {
            newEntity.setPasswordHash(existingEntity.getPasswordHash());
        } else if (!newEntity.getPasswordHash().startsWith("$2a$")) {
            // Encode password mới nếu chưa được encode
            newEntity.setPasswordHash(passwordEncoder.encode(newEntity.getPasswordHash()));
        }
    }

    @Override
    protected void beforeSoftDelete(Staff entity) {
        log.info("Soft deleting staff: {}", entity.getStaffId());
    }
}