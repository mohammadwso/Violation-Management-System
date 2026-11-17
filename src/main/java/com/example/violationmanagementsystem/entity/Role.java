package com.example.violationmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name_ar", nullable = false, unique = true, length = 50)
    private String nameAr;

    @Column(name = "name_en", nullable = false, unique = true, length = 50)
    private String nameEn;

    @Column(nullable = false, unique = true, length = 10)
    private Integer level;

    @Column(nullable = false, unique = true, length = 10)
    private boolean active;
}

