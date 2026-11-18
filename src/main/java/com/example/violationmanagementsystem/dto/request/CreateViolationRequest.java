package com.example.violationmanagementsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Date;

public record CreateViolationRequest(
        @NotNull
        @Length(min = 4)
        Float latitude,
        @NotNull
        @Length(min = 4)
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
