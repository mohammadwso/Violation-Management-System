package com.example.violationmanagementsystem.controller;

import com.example.violationmanagementsystem.dto.request.ViolationImageRequest;
import com.example.violationmanagementsystem.dto.response.ViolationImageResponse;
import com.example.violationmanagementsystem.security.AppUserDetails;
import com.example.violationmanagementsystem.service.ViolationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/violations")
public class ViolationsController {

    @Autowired
    ViolationService violationService;

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ViolationImageResponse uploadViolationImage(
            @RequestParam("image") ViolationImageRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) throws IOException {
        return violationService.storeImage(request.image(), userDetails);
    }
}
