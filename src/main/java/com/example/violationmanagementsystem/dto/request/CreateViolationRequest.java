package com.example.violationmanagementsystem.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Date;

public record CreateViolationRequest(
        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Float latitude,
        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Float longitude,
        @NotNull
        Long regionId,
        @NotNull
        @Length(min = 4)
        String plateNumber,
        @NotNull
        @Length(min = 10, max = 10)
        String violatorId,
        @NotNull
        Integer violationTypeId,
        @NotNull
        String violationImageKey,
        @NotNull
        Date violationDate,
        String inspectorNote
) {
}
