package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.ViolationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViolationTypeRepo extends JpaRepository<ViolationType, Integer> {
}
