package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepo extends JpaRepository<Violation, Long> {
    List<Violation> findByAssignedOperatorId(Long id);
    List<Violation> findByAssignedOperatorIdAndStatusId(Long id, Long StatusId);

}
