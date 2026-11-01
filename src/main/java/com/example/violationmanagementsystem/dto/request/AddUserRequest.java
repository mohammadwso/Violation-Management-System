package com.example.violationmanagementsystem.dto.request;

import jakarta.validation.constraints.*;

public record AddUserRequest(
        @NotBlank
        @Size(min = 5, max = 50)
        String username,
        @NotBlank
        @Size(min = 8, max = 32)
        String password,
        @NotBlank
        @Pattern(regexp = "^[12]\\d{9}$", message = "National id must be 10 digits starting with 1 or 2")
        String nationalId,
        @NotBlank
        @Email
        @Size(max = 100)
        String email,
        @NotNull
        long roleId) {
}
