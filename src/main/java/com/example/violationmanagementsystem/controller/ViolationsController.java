package com.example.violationmanagementsystem.controller;

import com.example.violationmanagementsystem.dto.request.CreateViolationRequest;
import com.example.violationmanagementsystem.dto.request.ViolationImageRequest;
import com.example.violationmanagementsystem.dto.response.CreateViolationResponse;
import com.example.violationmanagementsystem.dto.response.GetViolationResponse;
import com.example.violationmanagementsystem.dto.response.ViolationImageResponse;
import com.example.violationmanagementsystem.security.AppUserDetails;
import com.example.violationmanagementsystem.service.ViolationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/violations")
public class ViolationsController {

    @Autowired
    ViolationService violationService;

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ViolationImageResponse uploadViolationImage(
            @Valid @ModelAttribute ViolationImageRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) throws IOException {
        return violationService.storeImage(request.image(), userDetails);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateViolationResponse createViolation(
            @RequestBody CreateViolationRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        return violationService.createViolation(request, userDetails);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public List<GetViolationResponse> getViolations(@AuthenticationPrincipal AppUserDetails userDetails) {
//        return violationService.getViolations(userDetails);
        List<GetViolationResponse> responses = violationService.getViolations(userDetails);
        System.out.println(responses);
        return responses;
    }

    @PostMapping("/claim")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole(OPERATOR)")
    public ResponseEntity<Boolean> claimViolations(@AuthenticationPrincipal AppUserDetails userDetails) {
//        return violationService.getViolations(userDetails);
        return ResponseEntity.ok(violationService.claimViolations(userDetails));

    }
}
