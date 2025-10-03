package com.shopacc.service.impl;

import com.shopacc.model.Accounts;
import com.shopacc.repository.AccountsRepository;
import com.shopacc.service.AccountsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountsServiceImpl implements AccountsService {

    private final AccountsRepository accountsRepository;

    public AccountsServiceImpl(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    @Override
    public Accounts save(Accounts account) {
        return accountsRepository.save(account);
    }

    @Override
    public Optional<Accounts> findById(Integer id) {
        return accountsRepository.findById(id);
    }

    @Override
    public List<Accounts> findAll() {
        return accountsRepository.findAll();
    }

    @Override
    public List<Accounts> findBySold(boolean sold) {
        return accountsRepository.findBySold(sold);
    }

    @Override
    public List<Accounts> findByGameType(String gameType) {
        return accountsRepository.findByGameType(gameType);
    }

    @Override
    public List<Accounts> findByPriceRange(Double min, Double max) {
        return accountsRepository.findByPriceBetween(min, max);
    }

    @Override
    public void deleteById(Integer id) {
        accountsRepository.deleteById(id);
    }
}
