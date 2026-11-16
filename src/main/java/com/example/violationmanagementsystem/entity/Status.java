package com.example.violationmanagementsystem.entity;

import com.example.violationmanagementsystem.enums.StatusCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
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


    public static Status from(StatusCode statusCode) {
        Status s = new Status();
        s.setCode(statusCode.name());
        switch (statusCode) {
            case NEW -> s.setId(1L);
            case ASSIGNED -> s.setId(2L);
            case APPROVED -> s.setId(3L);
            case REJECTED -> s.setId(4L);
        }
        return s;
    }



}
