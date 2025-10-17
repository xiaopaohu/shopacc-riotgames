package com.shopacc.repository;

import com.shopacc.model.entity.Account;
import com.shopacc.repository.base.SoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends SoftDeletableRepository<Account, Integer> {

    // Find by username
    Optional<Account> findByUsernameAndDeletedAtIsNull(String username);

    // Find by game type
    List<Account> findByGameTypeAndDeletedAtIsNull(String gameType);
    Page<Account> findByGameTypeAndDeletedAtIsNull(String gameType, Pageable pageable);

    // Find by status
    List<Account> findByStatusAndDeletedAtIsNull(String status);
    Page<Account> findByStatusAndDeletedAtIsNull(String status, Pageable pageable);

    // Find by game type and status
    List<Account> findByGameTypeAndStatusAndDeletedAtIsNull(String gameType, String status);
    Page<Account> findByGameTypeAndStatusAndDeletedAtIsNull(String gameType, String status, Pageable pageable);

    // Find by price range
    List<Account> findByPriceBetweenAndDeletedAtIsNull(BigDecimal minPrice, BigDecimal maxPrice);

    // Check username exists
    boolean existsByUsernameAndDeletedAtIsNull(String username);

    // Count by game type
    long countByGameTypeAndDeletedAtIsNull(String gameType);

    // Count by status
    long countByStatusAndDeletedAtIsNull(String status);

    // Count by game type and status
    long countByGameTypeAndStatusAndDeletedAtIsNull(String gameType, String status);

    // Search by keyword
    @Query("SELECT a FROM Account a WHERE a.deletedAt IS NULL " +
            "AND (LOWER(a.accountName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.username) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.gameType) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Account> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Filter accounts
    @Query("SELECT a FROM Account a WHERE a.deletedAt IS NULL " +
            "AND (:gameType IS NULL OR a.gameType = :gameType) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:minPrice IS NULL OR a.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR a.price <= :maxPrice) " +
            "AND (:keyword IS NULL OR LOWER(a.accountName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.username) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Account> filterAccounts(@Param("gameType") String gameType,
                                 @Param("status") String status,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    // Get top accounts by price
    @Query("SELECT a FROM Account a WHERE a.deletedAt IS NULL AND a.status = 'AVAILABLE' ORDER BY a.price DESC")
    List<Account> findTopAccountsByPrice(Pageable pageable);

    // Get featured accounts
    @Query("SELECT a FROM Account a WHERE a.deletedAt IS NULL AND a.status = 'AVAILABLE' ORDER BY a.listedAt DESC")
    List<Account> findFeaturedAccounts(Pageable pageable);

    // Get total accounts value
    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Account a WHERE a.deletedAt IS NULL AND a.status = 'AVAILABLE'")
    BigDecimal getTotalAccountsValue();

    // Get total accounts value by game type
    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Account a WHERE a.deletedAt IS NULL " +
            "AND a.status = 'AVAILABLE' AND a.gameType = :gameType")
    BigDecimal getTotalAccountsValueByGameType(@Param("gameType") String gameType);

    // Get all distinct game types
    @Query("SELECT DISTINCT a.gameType FROM Account a WHERE a.deletedAt IS NULL ORDER BY a.gameType")
    List<String> findAllGameTypes();
}