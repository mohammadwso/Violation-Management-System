package com.example.violationmanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

// TODO: image validation
public record ViolationImageRequest(@NotNull MultipartFile image) {
}
