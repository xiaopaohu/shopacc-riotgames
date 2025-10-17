package com.shopacc.service.base;

import com.shopacc.repository.base.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Abstract Base Service Implementation
 * Cung cấp implementation mặc định cho các CRUD operations
 */
@Transactional
public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    protected final BaseRepository<T, ID> repository;

    protected BaseServiceImpl(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public T getById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        getEntityName() + " not found with id: " + id));
    }

    @Override
    public T create(T entity) {
        beforeCreate(entity);
        T savedEntity = repository.save(entity);
        afterCreate(savedEntity);
        return savedEntity;
    }

    @Override
    public T update(ID id, T entity) {
        T existingEntity = getById(id);
        beforeUpdate(existingEntity, entity);
        T updatedEntity = repository.save(entity);
        afterUpdate(updatedEntity);
        return updatedEntity;
    }

    @Override
    public void deleteById(ID id) {
        T entity = getById(id);
        beforeDelete(entity);
        repository.deleteById(id);
        afterDelete(entity);
    }

    @Override
    public void delete(T entity) {
        beforeDelete(entity);
        repository.delete(entity);
        afterDelete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        return repository.saveAll(entities);
    }

    // Hook methods - có thể override trong subclass
    protected void beforeCreate(T entity) {}
    protected void afterCreate(T entity) {}
    protected void beforeUpdate(T existingEntity, T newEntity) {}
    protected void afterUpdate(T entity) {}
    protected void beforeDelete(T entity) {}
    protected void afterDelete(T entity) {}

    // Abstract method để lấy tên entity (dùng cho error message)
    protected abstract String getEntityName();
}