package com.example.violationmanagementsystem.controller;

import com.example.violationmanagementsystem.dto.request.ViolationImageRequest;
import com.example.violationmanagementsystem.dto.response.ViolationImageResponse;
import com.example.violationmanagementsystem.security.AppUserDetails;
import com.example.violationmanagementsystem.service.ViolationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/violations")
public class ViolationsController {

    @Autowired
    ViolationService violationService;

    @PostMapping("/images")
    public ViolationImageResponse uploadViolationImage(@Valid ViolationImageRequest request, @AuthenticationPrincipal AppUserDetails userDetails) {
        return violationService.storeImage(request, userDetails);
    }
}
