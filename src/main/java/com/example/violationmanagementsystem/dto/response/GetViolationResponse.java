package com.example.violationmanagementsystem.dto.response;

import com.example.violationmanagementsystem.entity.ManualViolation;
import com.example.violationmanagementsystem.entity.Violation;
import com.example.violationmanagementsystem.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

import static com.example.violationmanagementsystem.mapper.ViolationMapper.getInspectorNote;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetViolationResponse(
        Long id,
        Float latitude,
        Float longitude,
        Long regionId,
        Long statusId,
        String plateNumber,
        String violatorId,
        Long violationTypeId,
        String violationImageKey,
        Date violationDate,
        Long assignedOperatorId,
        Date assignedAt,
        Date decidedAt,
        Date violationCreationDate,
        Date violationUpdateDate,
        Long inspectorId,
        String cameraId,
        String inspectorNote,
        String operatorNote
) {
    public static GetViolationResponse from(Violation violation) {
        return new GetViolationResponse(
                violation.getId(),
                violation.getLatitude(),
                violation.getLongitude(),
                violation.getRegion().getId(),
                violation.getStatus().getId(),
                violation.getPlateNumber(),
                violation.getViolatorId(),
                violation.getViolationType().getId(),
                violation.getViolationImageKey(),
                violation.getViolationDate(),
                violation.getAssignedOperator() != null ? violation.getAssignedOperator().getId() : null,
                violation.getAssignedAt(),
                violation.getDecidedAt(),
                violation.getCreatedAt(),
                violation.getUpdatedAt(),
                violation instanceof ManualViolation manualViolation
                        ? manualViolation.getInspector().getId() : null,
                violation instanceof ManualViolation manualViolation
                        ? null : "someId",
                getInspectorNote(violation),
                getInspectorNote(violation)
        );

    }
}
