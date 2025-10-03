package com.shopacc.service;

import com.shopacc.model.Accounts;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountsService {
    Accounts save(Accounts account);
    Optional<Accounts> findById(Integer id);
    List<Accounts> findAll();
    List<Accounts> findBySold(boolean sold);
    List<Accounts> findByGameType(String gameType);
    List<Accounts> findByRank(String rank);
    List<Accounts> findByPriceRange(BigDecimal min, BigDecimal max);
    void deleteById(Integer id);
}
