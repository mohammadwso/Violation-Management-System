package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.Region;
import com.example.violationmanagementsystem.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViolationRepo extends JpaRepository<Violation, Integer> {
}
