package com.shopacc.repository;

import com.shopacc.model.entity.Staff;
import com.shopacc.repository.base.SoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends SoftDeletableRepository<Staff, Integer> {

    /**
     * Tìm staff theo email
     */
    Optional<Staff> findByEmailAndDeletedAtIsNull(String email);

    /**
     * Tìm staff theo role
     */
    List<Staff> findByRoleAndDeletedAtIsNull(String role);

    /**
     * Tìm staff theo role với phân trang
     */
    Page<Staff> findByRoleAndDeletedAtIsNull(String role, Pageable pageable);

    /**
     * Lấy tất cả staff chưa bị xóa (THÊM METHOD NÀY)
     */
    List<Staff> findAllByDeletedAtIsNull();

    /**
     * Tìm kiếm staff theo tên hoặc email
     */
    @Query("SELECT s FROM Staff s WHERE s.deletedAt IS NULL AND " +
            "(LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Staff> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * Đếm số staff theo role
     */
    long countByRoleAndDeletedAtIsNull(String role);

    /**
     * Lấy danh sách staff theo nhiều role
     */
    @Query("SELECT s FROM Staff s WHERE s.deletedAt IS NULL AND s.role IN :roles")
    List<Staff> findByRolesIn(@Param("roles") List<String> roles);

    /**
     * Đếm tất cả staff chưa bị xóa
     */
    long countByDeletedAtIsNull();

    boolean existsByEmail(String email);
}