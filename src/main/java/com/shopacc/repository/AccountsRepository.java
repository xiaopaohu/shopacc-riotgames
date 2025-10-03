package com.shopacc.repository;

import com.shopacc.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
    List<Accounts> findBySold(boolean sold);
    List<Accounts> findByGameType(String gameType);
    List<Accounts> findByPriceBetween(Double min, Double max);
}
