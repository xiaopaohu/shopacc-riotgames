package com.shopacc.service;

import com.shopacc.dto.request.SearchRequest;
import com.shopacc.dto.response.PageResponse;
import com.shopacc.model.entity.Staff;
import com.shopacc.service.base.SoftDeletableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StaffService extends SoftDeletableService<Staff, Integer> {

    /**
     * Tìm staff theo email
     */
    Optional<Staff> findByEmail(String email);

    /**
     * Tìm staff theo role
     */
    List<Staff> findByRole(String role);

    /**
     * Tìm staff theo role với phân trang
     */
    Page<Staff> findByRole(String role, Pageable pageable);

    /**
     * Tìm kiếm staff theo keyword
     */
    PageResponse<Staff> search(SearchRequest request);

    /**
     * Kiểm tra email đã tồn tại
     */
    boolean existsByEmail(String email);

    /**
     * Đếm staff theo role
     */
    long countByRole(String role);

    /**
     * Lấy staff theo nhiều roles
     */
    List<Staff> findByRoles(List<String> roles);

    /**
     * Thay đổi password
     */
    Staff changePassword(Integer staffId, String oldPassword, String newPassword);

    /**
     * Reset password
     */
    Staff resetPassword(Integer staffId, String newPassword);

    /**
     * Cập nhật role
     */
    Staff updateRole(Integer staffId, String newRole);
}