package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.ManualViolation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManualViolationRepo extends JpaRepository<ManualViolation, Long> {
    List<ManualViolation> findByInspectorId(Long inspectorId);

    ManualViolation findByIdAndInspectorId(Long id, Long inspectorId);
}
