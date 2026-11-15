package com.example.violationmanagementsystem.entity;

import jakarta.persistence.*;
import org.hibernate.boot.internal.Abstract;

import java.util.Date;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_operator_id", nullable = true)
    private User assignedOperator;
    @Column(name = "assigned_at")
    private Date assignedAt;
    @Column(name = "decided_at")
    private Date decidedAt;

    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getViolatorId() {
        return violatorId;
    }

    public void setViolatorId(String violatorId) {
        this.violatorId = violatorId;
    }

    public String getViolationImageKey() {
        return violationImageKey;
    }

    public void setViolationImageKey(String violationImageKey) {
        this.violationImageKey = violationImageKey;
    }

    public Date getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(Date violationDate) {
        this.violationDate = violationDate;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public User getAssignedOperator() {
        return assignedOperator;
    }

    public void setAssignedOperator(User assignedOperator) {
        this.assignedOperator = assignedOperator;
    }

    public Date getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Date getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Date decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Violation() {
    }

    public Violation(Float latitude,
                     Float longitude,
                     String plateNumber,
                     String violatorId,
                     String violationImageKey,
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
        this.violationImageKey = violationImageKey;
        this.violationDate = violationDate;
        this.region = region;
        this.status = status;
        this.source = source;
    }
}
