package com.shopacc.repository.base;

import com.shopacc.model.entity.base.SoftDeletableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Base Repository cho entities có Soft Delete
 */
@NoRepositoryBean
public interface SoftDeletableRepository<T extends SoftDeletableEntity, ID> extends BaseRepository<T, ID> {

    /**
     * Tìm tất cả entities chưa bị xóa
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    List<T> findAllActive();

    /**
     * Tìm tất cả entities chưa bị xóa với phân trang
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    Page<T> findAllActive(Pageable pageable);

    /**
     * Tìm entity theo ID và chưa bị xóa
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL AND e.id = ?1")
    Optional<T> findByIdAndNotDeleted(ID id);

    /**
     * Đếm số lượng entities chưa bị xóa
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    long countActive();

    /**
     * Tìm tất cả entities đã bị xóa
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NOT NULL")
    List<T> findAllDeleted();

    /**
     * Tìm tất cả entities đã bị xóa với phân trang
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NOT NULL")
    Page<T> findAllDeleted(Pageable pageable);
}