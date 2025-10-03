package com.shopacc.repository;

import com.shopacc.model.Staffs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffsRepository extends JpaRepository<Staffs, Integer> {
    Optional<Staffs> findByEmail(String email);
    List<Staffs> findByRole(String role);
}
