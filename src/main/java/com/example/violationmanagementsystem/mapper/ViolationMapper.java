package com.example.violationmanagementsystem.mapper;

import com.example.violationmanagementsystem.entity.Violation;
import com.example.violationmanagementsystem.entity.ViolationNote;
import com.example.violationmanagementsystem.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViolationMapper {

    public static String getRoleNote(Violation violation, UserRole role) {
        log.info("MAPPER violationNotes: {}", violation.getViolationNotes());
        return violation.getViolationNotes().stream()
                .filter(violationNote -> role.name().equals(violationNote.getUser().getRole().getCode().toString()))
                .map(ViolationNote::getViolationNote).findFirst().orElse(null);
    }
}
