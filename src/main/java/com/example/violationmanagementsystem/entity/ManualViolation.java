package com.example.violationmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "violations_manual")
public class ManualViolation extends Violation {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private User inspector;

    public ManualViolation() {
    }

    public ManualViolation(Float latitude, Float longitude, String plateNumber, String violatorId, Date violationDate, ViolationType violationType, Region region, Status status, Source source, User inspector) {
        super(latitude, longitude, plateNumber, violatorId, violationDate, violationType, region, status, source);
        this.inspector = inspector;
    }
}
