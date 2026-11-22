package com.example.violationmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.boot.internal.Abstract;
import org.jspecify.annotations.NonNull;

import java.util.*;

@Data
@Entity
@Table(name = "violations")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude", nullable = false)
    private Float latitude;
    @Column(name = "longitude", nullable = false)
    private Float longitude;

    @Column(name = "plate_number")
    private String plateNumber;

    @Column(name = "violator_id")
    private String violatorId;
    @Column(name = "violation_image_key")
    private String violationImageKey;

    @Column(name = "violation_date")
    private Date violationDate;

    @ManyToOne
    @JoinColumn(name = "violation_type_id")
    private ViolationType violationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id")
    private Region region;

    @Column(name = "decided_at")
    private Date decidedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @NonNull
    @OneToMany(mappedBy = "violation", cascade = {CascadeType.ALL})
    private List<ViolationNote> violationNotes = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_assignment_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ViolationAssignmentHistory currentAssignment;

    public Violation() {
    }

    public Violation(Float latitude,
                     Float longitude,
                     String plateNumber,
                     String violatorId,
                     Date violationDate,
                     ViolationType violationType,
                     Region region,
                     Status status,
                     Source source) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.plateNumber = plateNumber;
        this.violationType = violationType;
        this.violatorId = violatorId;
        this.violationDate = violationDate;
        this.region = region;
        this.status = status;
        this.source = source;
    }
    public void addNote(ViolationNote violationNote) {
        violationNotes.add(violationNote);
        violationNote.setViolation(this);
    }
}
