package com.shopacc.service.base;

import com.shopacc.model.entity.base.SoftDeletableEntity;
import com.shopacc.repository.base.SoftDeletableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Abstract Soft Deletable Service Implementation
 */
@Transactional
public abstract class SoftDeletableServiceImpl<T extends SoftDeletableEntity, ID>
        extends BaseServiceImpl<T, ID>
        implements SoftDeletableService<T, ID> {

    protected final SoftDeletableRepository<T, ID> softDeletableRepository;

    protected SoftDeletableServiceImpl(SoftDeletableRepository<T, ID> repository) {
        super(repository);
        this.softDeletableRepository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllActive() {
        return softDeletableRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAllActive(Pageable pageable) {
        return softDeletableRepository.findAllActive(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findActiveById(ID id) {
        return softDeletableRepository.findByIdAndNotDeleted(id);
    }

    @Override
    @Transactional(readOnly = true)
    public T getActiveById(ID id) {
        return softDeletableRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        getEntityName() + " not found or deleted with id: " + id));
    }

    @Override
    public void softDeleteById(ID id) {
        T entity = getActiveById(id);
        softDelete(entity);
    }

    @Override
    public void softDelete(T entity) {
        beforeSoftDelete(entity);
        entity.softDelete();
        repository.save(entity);
        afterSoftDelete(entity);
    }

    @Override
    public T restore(ID id) {
        T entity = getById(id);
        if (entity.getDeletedAt() == null) {
            throw new IllegalStateException(getEntityName() + " is not deleted");
        }
        beforeRestore(entity);
        entity.restore();
        T restoredEntity = repository.save(entity);
        afterRestore(restoredEntity);
        return restoredEntity;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllDeleted() {
        return softDeletableRepository.findAllDeleted();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAllDeleted(Pageable pageable) {
        return softDeletableRepository.findAllDeleted(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        return softDeletableRepository.countActive();
    }

    // Override deleteById để mặc định là soft delete
    @Override
    public void deleteById(ID id) {
        softDeleteById(id);
    }

    @Override
    public void delete(T entity) {
        softDelete(entity);
    }

    // Hook methods cho soft delete
    protected void beforeSoftDelete(T entity) {}
    protected void afterSoftDelete(T entity) {}
    protected void beforeRestore(T entity) {}
    protected void afterRestore(T entity) {}
}