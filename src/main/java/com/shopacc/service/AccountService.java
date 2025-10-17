package com.shopacc.service;

import com.shopacc.model.entity.Account;
import com.shopacc.service.base.SoftDeletableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService extends SoftDeletableService<Account, Integer> {

    Optional<Account> findByUsername(String username);

    List<Account> findByGameType(String gameType);
    Page<Account> findByGameType(String gameType, Pageable pageable);

    List<Account> findByStatus(String status);

    List<Account> findByGameTypeAndStatus(String gameType, String status);

    List<Account> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    Page<Account> searchByKeyword(String keyword, Pageable pageable);

    Page<Account> filterAccounts(String gameType, String status, BigDecimal minPrice,
                                 BigDecimal maxPrice, String keyword, Pageable pageable);

    boolean existsByUsername(String username);

    long countByGameType(String gameType);
    long countByStatus(String status);
    long countByGameTypeAndStatus(String gameType, String status);

    List<Account> findTopAccountsByPrice(int limit);
    List<Account> findFeaturedAccounts(int limit);
    List<Account> findAvailableAccounts();

    Account updateStatus(Integer accountId, String status);
    Account updatePrice(Integer accountId, BigDecimal newPrice);
    Account markAsSold(Integer accountId);
    Account markAsDelivered(Integer accountId);

    BigDecimal getTotalAccountsValue();
    BigDecimal getTotalAccountsValueByGameType(String gameType);
    List<String> getAllGameTypes();
}