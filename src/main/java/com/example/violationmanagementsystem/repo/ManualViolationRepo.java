package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.ManualViolation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManualViolationRepo extends JpaRepository<ManualViolation, Integer> {
}
