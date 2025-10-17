package com.shopacc.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Base Service Interface cho tất cả services
 * Định nghĩa các operations cơ bản: CRUD, Pagination, Search
 */
public interface BaseService<T, ID> {

    /**
     * Lấy tất cả entities
     */
    List<T> findAll();

    /**
     * Lấy tất cả entities với phân trang
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Tìm entity theo ID
     */
    Optional<T> findById(ID id);

    /**
     * Lấy entity theo ID (throw exception nếu không tìm thấy)
     */
    T getById(ID id);

    /**
     * Tạo mới entity
     */
    T create(T entity);

    /**
     * Cập nhật entity
     */
    T update(ID id, T entity);

    /**
     * Xóa entity theo ID
     */
    void deleteById(ID id);

    /**
     * Xóa entity
     */
    void delete(T entity);

    /**
     * Kiểm tra entity có tồn tại không
     */
    boolean existsById(ID id);

    /**
     * Đếm tổng số entities
     */
    long count();

    /**
     * Lưu một list entities
     */
    List<T> saveAll(List<T> entities);
}