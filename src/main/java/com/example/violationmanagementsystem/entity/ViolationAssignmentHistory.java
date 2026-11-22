package com.example.violationmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "violation_assignment_history")
public class ViolationAssignmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "violation_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Violation violation;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;

    @NotNull
    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Size(max = 20)
    @Column(name = "outcome", length = 20)
    private String outcome;


    @ColumnDefault("sysutcdatetime()")
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}
