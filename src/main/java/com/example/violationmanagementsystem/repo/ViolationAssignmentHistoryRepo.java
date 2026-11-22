package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.Violation;
import com.example.violationmanagementsystem.entity.ViolationAssignmentHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViolationAssignmentHistoryRepo extends JpaRepository<ViolationAssignmentHistory, Long> {
//    @EntityGraph(attributePaths = {"violation", "operator"})
//    Optional<ViolationAssignmentHistory> findByViolationIdAndOperatorIdAndOutcomeIsNull(Long id, Long operatorId);
}
