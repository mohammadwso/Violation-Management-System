package com.example.violationmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ref_status")
public class Status {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;
    @Column(name = "name_ar", nullable = false, unique = true)
    private String nameAr;
    @Column(name = "name_en", nullable = false, unique = true)
    private String nameEn;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public String getCode() {
        return code;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    // Do we need setters ?



}
