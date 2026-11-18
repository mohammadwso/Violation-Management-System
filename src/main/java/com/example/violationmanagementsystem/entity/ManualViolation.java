package com.example.violationmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "violations_manual")
public class ManualViolation extends Violation {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector;

    public ManualViolation() {
    }

    public ManualViolation(Float latitude, Float longitude, String plateNumber, String violatorId, Date violationDate, ViolationType violationType, Region region, Status status, Source source, User inspector) {
        super(latitude, longitude, plateNumber, violatorId, violationDate, violationType, region, status, source);
        this.inspector = inspector;
    }
}
