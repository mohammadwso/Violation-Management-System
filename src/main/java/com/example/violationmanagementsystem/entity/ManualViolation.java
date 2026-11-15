package com.example.violationmanagementsystem.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "violations_manual")
public class ManualViolation extends Violation {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private User inspector;

    public ManualViolation() {
    }

    public User getInspector() {
        return inspector;
    }

    public void setInspector(User inspector) {
        this.inspector = inspector;
    }

    public ManualViolation(Float latitude, Float longitude, String plateNumber, String violatorId, String violationImageKey, Date violationDate, ViolationType violationType, Region region, Status status, Source source, User inspector) {
        super(latitude, longitude, plateNumber, violatorId, violationImageKey, violationDate, violationType, region, status, source);
        this.inspector = inspector;
    }
}
