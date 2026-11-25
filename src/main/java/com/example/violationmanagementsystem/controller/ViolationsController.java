package com.example.violationmanagementsystem.controller;

import com.example.violationmanagementsystem.dto.request.CreateViolationRequest;
import com.example.violationmanagementsystem.dto.request.DecideViolationRequest;
import com.example.violationmanagementsystem.dto.request.ViolationImageRequest;
import com.example.violationmanagementsystem.dto.response.ApiResponse;
import com.example.violationmanagementsystem.dto.response.CreateViolationResponse;
import com.example.violationmanagementsystem.dto.response.GetViolationResponse;
import com.example.violationmanagementsystem.dto.response.ViolationImageResponse;
import com.example.violationmanagementsystem.enums.ResponseStatusCode;
import com.example.violationmanagementsystem.exception.BadRequestException;
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
    public ResponseEntity<?> uploadViolationImage(
            @Valid @ModelAttribute ViolationImageRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) throws IOException {
        return ApiResponse.getSuccessResponse(violationService.storeImage(request.image(), userDetails));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createViolation(
            @Valid @RequestBody CreateViolationRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) throws BadRequestException {
        return
                ApiResponse.getSuccessResponse(violationService.createViolation(request, userDetails));
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> getViolations(@AuthenticationPrincipal AppUserDetails userDetails) {
        return ApiResponse.getSuccessResponse(violationService.getViolations(userDetails));


//        List<GetViolationResponse> responses = violationService.getViolations(userDetails);
//        System.out.println(responses);
//        return responses;
    }

    @PostMapping("/claim")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> claimViolations(
            @AuthenticationPrincipal AppUserDetails userDetails) {
        return ApiResponse.getSuccessResponse(violationService.claimViolations(userDetails));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getViolation(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @PathVariable String id) {
        return ApiResponse.getSuccessResponse(violationService.getViolation(userDetails, id));
    }

    @PostMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> acceptViolation(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @PathVariable String id) {
        return ApiResponse.getSuccessResponse(violationService.decideViolation(id, userDetails, true, null));

    }

    @PostMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> rejectViolation(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @RequestBody(required = false) DecideViolationRequest request,
            @PathVariable String id) {
        return ApiResponse.getSuccessResponse(violationService.decideViolation(id, userDetails, false, request));
    }
}
