package com.example.violationmanagementsystem.service;

import com.example.violationmanagementsystem.dto.request.CreateViolationRequest;
import com.example.violationmanagementsystem.dto.response.CreateViolationResponse;
import com.example.violationmanagementsystem.dto.response.GetViolationResponse;

import com.example.violationmanagementsystem.dto.response.ViolationImageResponse;
import com.example.violationmanagementsystem.entity.*;
import com.example.violationmanagementsystem.enums.SourceCode;
import com.example.violationmanagementsystem.enums.StatusCode;
import com.example.violationmanagementsystem.enums.UserRole;
import com.example.violationmanagementsystem.exception.BadRequestException;
import com.example.violationmanagementsystem.repo.*;
import com.example.violationmanagementsystem.security.AppUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
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
        System.out.println(request);
        System.out.println(userDetails);
        Region region = regionRepo.findById(request.regionId())
                .filter(Region::isActive)
                .orElseThrow(() -> new BadRequestException("region doesn't exist"));
        ViolationType violationType = violationTypeRepo.findById(request.violationTypeId())
                .orElseThrow(() -> new BadRequestException("region doesn't exist"));
        Status status = Status.from(StatusCode.NEW);
        // use code or change violation table fk to use code
        System.out.println(status);

        Source source = sourceRepo.findByCode(SourceCode.MANUAL.name())
                .orElseThrow(() -> new RuntimeException("manual source doesnt exists"));

        User user = userRepo.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("user doesnt exists"));
        ;

        if (request.violationDate().after(new Date())) {
            throw new RuntimeException("invalid Date");
        }


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
        ManualViolation saved = manualViolationRepo.save(violation);

        return new CreateViolationResponse(true, saved.getId());
    }

    public List<GetViolationResponse> getViolations(AppUserDetails userDetails) {

        User user = userRepo.findUserByUsername(userDetails.getUsername()).orElseThrow();
        String userRole = user.getRole().getCode();
        log.info("Current User Role: {}. enum: {}", userRole, UserRole.OPERATOR.name());
        if (userRole.equals(UserRole.OPERATOR.name())) {
            assignViolations(userDetails.getId());
            return violationRepo.findByAssignedOperatorId(userDetails.getId()).stream().map(GetViolationResponse::from).toList();
        }

        return violationRepo.findAll().stream().map(GetViolationResponse::from).toList();
    }

    private void assignViolations(Long id) {
        List<Violation> violations = violationRepo.findByAssignedOperatorId(id);
        log.info("Currently Assigned Cases: {}", violations.toArray().length);
        if (!violations.isEmpty()) {
            return;
        }
        Optional<Violation> violation = violationRepo.findByAssignedOperatorId(null).stream().findFirst();
        if (violation.isEmpty()) {
            log.info("No Unassigned Cases");
            return;
        }
        violation.get().setAssignedOperator(userRepo.findById(id).orElseThrow());
        violation.get().setAssignedAt(new Date());
        log.info("Updating Violation: {}", violation);
        violationRepo.save(violation.get());
        log.info("Violation Assigned Successfully ");
        return;
    }
}
