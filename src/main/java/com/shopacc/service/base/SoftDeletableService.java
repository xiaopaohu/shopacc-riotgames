package com.shopacc.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface cho entities có Soft Delete
 */
public interface SoftDeletableService<T, ID> extends BaseService<T, ID> {

    /**
     * Lấy tất cả entities chưa bị xóa
     */
    List<T> findAllActive();

    /**
     * Lấy tất cả entities chưa bị xóa với phân trang
     */
    Page<T> findAllActive(Pageable pageable);

    /**
     * Tìm entity chưa bị xóa theo ID
     */
    Optional<T> findActiveById(ID id);

    /**
     * Lấy entity chưa bị xóa theo ID (throw exception nếu không tìm thấy)
     */
    T getActiveById(ID id);

    /**
     * Soft delete entity theo ID
     */
    void softDeleteById(ID id);

    /**
     * Soft delete entity
     */
    void softDelete(T entity);

    /**
     * Restore entity đã bị soft delete
     */
    T restore(ID id);

    /**
     * Lấy tất cả entities đã bị xóa
     */
    List<T> findAllDeleted();

    /**
     * Lấy tất cả entities đã bị xóa với phân trang
     */
    Page<T> findAllDeleted(Pageable pageable);

    /**
     * Đếm số entities chưa bị xóa
     */
    long countActive();
}