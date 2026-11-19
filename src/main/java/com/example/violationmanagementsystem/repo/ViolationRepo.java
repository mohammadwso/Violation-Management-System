package com.example.violationmanagementsystem.repo;

import com.example.violationmanagementsystem.entity.Violation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViolationRepo extends JpaRepository<Violation, Long> {

    @Override
    @EntityGraph(attributePaths = {"violationNotes", "violationNotes.user", "violationNotes.user.role"})
    Optional<Violation> findById(Long id);
    @EntityGraph(attributePaths = {"violationNotes", "violationNotes.user", "violationNotes.user.role"})
    List<Violation> findByAssignedOperatorId(Long id);
    @EntityGraph(attributePaths = {"violationNotes", "violationNotes.user", "violationNotes.user.role"})
    Optional<Violation> findByIdAndAssignedOperatorId(Long id, Long assignedOperatorId);
    @EntityGraph(attributePaths = {"violationNotes", "violationNotes.user", "violationNotes.user.role"})
    List<Violation> findByAssignedOperatorIdAndStatusId(Long id, Long StatusId);

}
