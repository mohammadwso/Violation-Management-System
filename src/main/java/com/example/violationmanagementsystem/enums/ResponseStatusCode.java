package com.example.violationmanagementsystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatusCode {
    SUCCESS(1001,"SUCCESS"),
    FAIL(2001,"FAIL");

    private final int id;
    private final String message;
}
