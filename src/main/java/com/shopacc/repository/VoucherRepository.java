package com.shopacc.repository;

import com.shopacc.model.entity.Voucher;
import com.shopacc.repository.base.SoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends SoftDeletableRepository<Voucher, Integer> {

    Optional<Voucher> findByCodeAndDeletedAtIsNull(String code);

    boolean existsByCodeAndDeletedAtIsNull(String code);

    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL AND v.isActive = true " +
            "AND :now BETWEEN v.validFrom AND v.validTo " +
            "AND (v.usageLimit IS NULL OR v.usedCount < v.usageLimit)")
    List<Voucher> findActiveVouchers(@Param("now") LocalDateTime now);

    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL AND v.isActive = true " +
            "AND :now BETWEEN v.validFrom AND v.validTo " +
            "AND (v.usageLimit IS NULL OR v.usedCount < v.usageLimit)")
    Page<Voucher> findActiveVouchers(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL " +
            "AND (LOWER(v.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Voucher> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL AND v.isActive = true " +
            "AND v.validTo BETWEEN :now AND :endDate")
    List<Voucher> findExpiringVouchers(@Param("now") LocalDateTime now, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(v) FROM Voucher v WHERE v.deletedAt IS NULL AND v.isActive = true " +
            "AND :now BETWEEN v.validFrom AND v.validTo " +
            "AND (v.usageLimit IS NULL OR v.usedCount < v.usageLimit)")
    long countActiveVouchers(@Param("now") LocalDateTime now);
}