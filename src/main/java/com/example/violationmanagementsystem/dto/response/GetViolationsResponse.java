package com.example.violationmanagementsystem.dto.response;

public record GetViolationsResponse(
        Long id,
        Long sourceId,
        Long StatusId
) {
}
