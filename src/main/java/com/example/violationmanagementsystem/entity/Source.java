package com.example.violationmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ref_source")
public class Source {
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

}
