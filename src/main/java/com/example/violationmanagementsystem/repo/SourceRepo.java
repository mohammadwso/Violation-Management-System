package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.Region;
import com.example.violationmanagementsystem.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceRepo extends JpaRepository<Source, Long> {
    Optional<Source> findByCode(String code);
}
