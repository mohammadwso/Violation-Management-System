package com.example.violationmanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Date;

public record CreateViolationRequest(
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Float latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Float longitude,
        @NotNull Long regionId,
        @NotBlank @Length(min = 4, max = 10) String plateNumber,
        @NotBlank @Length(min = 10, max = 10) String violatorId,
        @NotNull Integer violationTypeId,
        @NotBlank String violationImageKey,
        @NotNull @PastOrPresent Date violationDate,
        String inspectorNote
) {
}
