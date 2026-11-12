package com.example.violationmanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.File;
import java.io.InputStream;

// TODO: image validation
public record ViolationImageRequest(InputStream image) {
}
