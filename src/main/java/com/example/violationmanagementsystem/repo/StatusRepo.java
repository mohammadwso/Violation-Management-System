package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepo extends JpaRepository<Status, Long> {

    Optional<Status> findByCode(String code);
}
