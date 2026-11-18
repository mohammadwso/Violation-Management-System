package com.example.violationmanagementsystem.mapper;

import com.example.violationmanagementsystem.entity.Violation;
import com.example.violationmanagementsystem.entity.ViolationNote;
import com.example.violationmanagementsystem.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViolationMapper {

    public static String getInspectorNote(Violation violation) {
        log.info(violation.getViolationNotes().toString());
        return violation.getViolationNotes().stream()
                .filter(violationNote -> violationNote.getUser().getRole().getCode() == UserRole.INSPECTOR.name())
                .map(ViolationNote::getViolationNote).findFirst().orElse(null);
    }
}
