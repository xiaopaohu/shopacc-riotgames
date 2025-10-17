package com.shopacc.repository.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Base Repository interface cho tất cả repositories
 * Kế thừa JpaRepository và JpaSpecificationExecutor để có đầy đủ tính năng
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Tìm tất cả entities không bị xóa (cho entities có soft delete)
     * Default method - có thể override trong sub-repository
     */
    default List<T> findAllActive() {
        return findAll();
    }

    /**
     * Tìm tất cả entities không bị xóa với phân trang
     */
    default Page<T> findAllActive(Pageable pageable) {
        return findAll(pageable);
    }
}