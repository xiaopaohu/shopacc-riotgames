package com.shopacc.service.impl;

import com.shopacc.model.Staffs;
import com.shopacc.repository.StaffsRepository;
import com.shopacc.service.StaffsService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StaffsServiceImpl implements StaffsService {

    private final StaffsRepository staffsRepository;

    public StaffsServiceImpl(StaffsRepository staffsRepository) {
        this.staffsRepository = staffsRepository;
    }

    @Override
    public Staffs save(Staffs staff) {
        return staffsRepository.save(staff);
    }

    @Override
    public Optional<Staffs> findById(Integer id) {
        return staffsRepository.findById(id);
    }

    @Override
    public Optional<Staffs> findByEmail(String email) {
        return staffsRepository.findByEmail(email);
    }

    @Override
    public List<Staffs> findByRole(String role) {
        return staffsRepository.findByRole(role);
    }

    @Override
    public List<Staffs> findAll() {
        return staffsRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        staffsRepository.deleteById(id);
    }
}
