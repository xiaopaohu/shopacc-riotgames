package com.shopacc.service.impl;

import com.shopacc.model.entity.Account;
import com.shopacc.repository.AccountRepository;
import com.shopacc.service.AccountService;
import com.shopacc.service.base.SoftDeletableServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AccountServiceImpl extends SoftDeletableServiceImpl<Account, Integer> implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        super(accountRepository);
        this.accountRepository = accountRepository;
    }

    @Override
    protected String getEntityName() {
        return "Account";
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByUsername(String username) {
        log.debug("Finding account by username: {}", username);
        return accountRepository.findByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByGameType(String gameType) {
        log.debug("Finding accounts by game type: {}", gameType);
        return accountRepository.findByGameTypeAndDeletedAtIsNull(gameType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Account> findByGameType(String gameType, Pageable pageable) {
        log.debug("Finding accounts by game type: {} with pagination", gameType);
        return accountRepository.findByGameTypeAndDeletedAtIsNull(gameType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByStatus(String status) {
        log.debug("Finding accounts by status: {}", status);
        return accountRepository.findByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByGameTypeAndStatus(String gameType, String status) {
        log.debug("Finding accounts by game type: {} and status: {}", gameType, status);
        return accountRepository.findByGameTypeAndStatusAndDeletedAtIsNull(gameType, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Finding accounts with price between {} and {}", minPrice, maxPrice);
        return accountRepository.findByPriceBetweenAndDeletedAtIsNull(minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Account> searchByKeyword(String keyword, Pageable pageable) {
        log.debug("Searching accounts with keyword: {}", keyword);
        return accountRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Account> filterAccounts(String gameType, String status, BigDecimal minPrice,
                                        BigDecimal maxPrice, String keyword, Pageable pageable) {
        log.debug("Filtering accounts");
        return accountRepository.filterAccounts(gameType, status, minPrice, maxPrice, keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByGameType(String gameType) {
        return accountRepository.countByGameTypeAndDeletedAtIsNull(gameType);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return accountRepository.countByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByGameTypeAndStatus(String gameType, String status) {
        return accountRepository.countByGameTypeAndStatusAndDeletedAtIsNull(gameType, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findTopAccountsByPrice(int limit) {
        log.debug("Finding top {} accounts by price", limit);
        return accountRepository.findTopAccountsByPrice(PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findFeaturedAccounts(int limit) {
        log.debug("Finding {} featured accounts", limit);
        return accountRepository.findFeaturedAccounts(PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAvailableAccounts() {
        return accountRepository.findByStatusAndDeletedAtIsNull("AVAILABLE");
    }

    @Override
    public Account updateStatus(Integer accountId, String status) {
        log.info("Updating status for account: {} to {}", accountId, status);
        Account account = getActiveById(accountId);
        account.setStatus(status);
        return accountRepository.save(account);
    }

    @Override
    public Account updatePrice(Integer accountId, BigDecimal newPrice) {
        log.info("Updating price for account: {} to {}", accountId, newPrice);
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        Account account = getActiveById(accountId);
        account.setPrice(newPrice);
        return accountRepository.save(account);
    }

    @Override
    public Account markAsSold(Integer accountId) {
        log.info("Marking account {} as sold", accountId);
        Account account = getActiveById(accountId);
        if (!"AVAILABLE".equals(account.getStatus())) {
            throw new IllegalStateException("Account is not available");
        }
        account.setStatus("SOLD");
        return accountRepository.save(account);
    }

    @Override
    public Account markAsDelivered(Integer accountId) {
        log.info("Marking account {} as delivered", accountId);
        Account account = getActiveById(accountId);
        if (!"SOLD".equals(account.getStatus())) {
            throw new IllegalStateException("Only sold accounts can be delivered");
        }
        account.setStatus("DELIVERED");
        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAccountsValue() {
        BigDecimal total = accountRepository.getTotalAccountsValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAccountsValueByGameType(String gameType) {
        BigDecimal total = accountRepository.getTotalAccountsValueByGameType(gameType);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllGameTypes() {
        return accountRepository.findAllGameTypes();
    }

    @Override
    protected void beforeCreate(Account entity) {
        if (existsByUsername(entity.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (entity.getStatus() == null) {
            entity.setStatus("AVAILABLE");
        }
        if (entity.getListedAt() == null) {
            entity.setListedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void beforeUpdate(Account existingEntity, Account newEntity) {
        if (!existingEntity.getUsername().equals(newEntity.getUsername())) {
            if (existsByUsername(newEntity.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
        }
    }
}