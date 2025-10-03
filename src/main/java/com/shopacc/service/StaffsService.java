package com.shopacc.service;

import com.shopacc.model.Staffs;
import java.util.List;
import java.util.Optional;

public interface StaffsService {
    Staffs save(Staffs staff);
    Optional<Staffs> findById(Integer id);
    Optional<Staffs> findByEmail(String email);
    List<Staffs> findByRole(String role);
    List<Staffs> findAll();
    void deleteById(Integer id);
}
