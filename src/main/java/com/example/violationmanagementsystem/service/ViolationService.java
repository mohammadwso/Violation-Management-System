package com.example.violationmanagementsystem.service;

import com.example.violationmanagementsystem.dto.request.CreateViolationRequest;
import com.example.violationmanagementsystem.dto.request.ViolationImageRequest;
import com.example.violationmanagementsystem.dto.response.CreateViolationResponse;
import com.example.violationmanagementsystem.dto.response.ViolationImageResponse;
import com.example.violationmanagementsystem.entity.*;
import com.example.violationmanagementsystem.enums.SourceCode;
import com.example.violationmanagementsystem.enums.StatusCode;
import com.example.violationmanagementsystem.exception.BadRequestException;
import com.example.violationmanagementsystem.repo.*;
import com.example.violationmanagementsystem.security.AppUserDetails;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class ViolationService {

    @Autowired
    StorageService storageService;

    @Autowired
    RegionRepo regionRepo;

    @Autowired
    ViolationTypeRepo violationTypeRepo;

    @Autowired
    SourceRepo sourceRepo;

    @Autowired
    StatusRepo statusRepo;

    @Autowired
    ViolationRepo violationRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ManualViolationRepo manualViolationRepo;

    public ViolationImageResponse storeImage(MultipartFile image, AppUserDetails userDetails) throws IOException {
        Source source = sourceRepo.findByCode(SourceCode.MANUAL.name()).orElseThrow(() -> new RuntimeException("error"));
        String key = storageService.createStagingKey(source.getCode());
        boolean exists = this.storageService.exists(key);
        if (exists) {
            throw new RuntimeException("image already exists");
        }
        // TODO: check if an object with this key already exists (storageService.exists(key)) before uploading
        String imageKey = this.storageService.upload(key, image, image.getSize(), image.getContentType());
        return new ViolationImageResponse(true, imageKey);
    }

    @Transactional
    public CreateViolationResponse createViolation(CreateViolationRequest request, AppUserDetails userDetails) {
        Region region = regionRepo.findById(request.regionId())
                .filter(Region::isActive)
                .orElseThrow(() -> new BadRequestException("region doesn't exist"));
        ViolationType violationType = violationTypeRepo.findById(request.violationTypeId())
                .orElseThrow(() -> new BadRequestException("region doesn't exist"));
        Status status = statusRepo.findByCode(StatusCode.NEW.name())
                .orElseThrow(() -> new RuntimeException("new status doesnt exists"));

        Source source = sourceRepo.findByCode(SourceCode.MANUAL.name())
                .orElseThrow(() -> new RuntimeException("manual source doesnt exists"));

        User user = userRepo.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("user doesnt exists"));;

        if (request.latitude() < 15.0 || request.latitude() > 33) {
            throw new RuntimeException("latitude out of bounds");
        }
        if (request.longitude() < 33.0 || request.longitude() > 57) {
            throw new RuntimeException("longitude out of bounds");
        }

        if (!violationTypeRepo.findById(request.violationTypeId()).orElseThrow().isActive()) {
            throw new RuntimeException("Inactive Violation");
        }

        String imageKey = request.violationImageKey();


        boolean exists = storageService.exists(imageKey);
        if (!exists) {
            throw new RuntimeException("image doesn't exists");
        }



        if (request.violationDate().after(new Date())) {
            throw new RuntimeException("invalid Date");
        }

        // String plateNumber,
        // String violatorId,


        ManualViolation violation = new ManualViolation(
                request.latitude(),
                request.longitude(),
                request.plateNumber(),
                request.violatorId(),
                request.violationDate(),
                violationType,
                region,
                status,
                source,
                user
                );
        violation.setCreatedAt(new Date());
        violation.setUpdatedAt(new Date());
        String finalKey = storageService.promote(imageKey);
        violation.setViolationImageKey(finalKey);
        ManualViolation saved =  manualViolationRepo.save(violation);

        return new CreateViolationResponse(true, saved.getId());
    }
}
