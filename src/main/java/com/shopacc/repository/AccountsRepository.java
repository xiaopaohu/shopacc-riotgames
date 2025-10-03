package com.shopacc.repository;

import com.shopacc.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
    List<Accounts> findBySold(boolean sold);
    List<Accounts> findByGameType(String gameType);
    List<Accounts> findByRank(String rank);
    List<Accounts> findByPriceBetween(BigDecimal min, BigDecimal max);
}
