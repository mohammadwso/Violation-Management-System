package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.Region;
import com.example.violationmanagementsystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepo extends JpaRepository<Region, Long> {
}
