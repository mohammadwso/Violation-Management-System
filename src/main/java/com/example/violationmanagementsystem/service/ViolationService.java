package com.example.violationmanagementsystem.service;

import com.example.violationmanagementsystem.dto.request.CreateViolationRequest;
import com.example.violationmanagementsystem.dto.response.CreateViolationResponse;
import com.example.violationmanagementsystem.dto.response.GetViolationResponse;

import com.example.violationmanagementsystem.dto.response.ViolationImageResponse;
import com.example.violationmanagementsystem.entity.*;
import com.example.violationmanagementsystem.enums.Outcome;
import com.example.violationmanagementsystem.enums.SourceCode;
import com.example.violationmanagementsystem.enums.StatusCode;
import com.example.violationmanagementsystem.enums.UserRole;
import com.example.violationmanagementsystem.exception.BadRequestException;
import com.example.violationmanagementsystem.exception.ErrorCode;
import com.example.violationmanagementsystem.exception.ForbiddenException;
import com.example.violationmanagementsystem.exception.base.AppException;
import com.example.violationmanagementsystem.repo.*;
import com.example.violationmanagementsystem.security.AppUserDetails;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

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
    ManualViolationRepo manualViolationRepo;

    @Autowired
    UserRepo userRepo;
    @Autowired
    ViolationAssignmentHistoryRepo assignmentHistoryRepo;

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
        log.info("Request: {}", request);
        log.info("Inspector Notes: {}", request.inspectorNote());
        Region region = regionRepo.findById(request.regionId())
                .filter(Region::isActive)
                .orElseThrow(() -> new BadRequestException("region doesn't exist"));
        ViolationType violationType = violationTypeRepo.findById(request.violationTypeId())
                .orElseThrow(() -> new BadRequestException("region doesn't exist"));
        Status status = Status.from(StatusCode.NEW);
        // use code or change violation table fk to use code
        Source source = sourceRepo.findByCode(SourceCode.MANUAL.name())
                .orElseThrow(() -> new RuntimeException("manual source doesnt exists"));

        User user = getUser(userDetails);

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

        if (request.inspectorNote() != null) {
            ViolationNote violationNote = new ViolationNote();
            violationNote.setUser(user);
            violationNote.setViolationNote(request.inspectorNote());
            violation.addNote(violationNote);
        }

        String finalKey = storageService.promote(imageKey);
        violation.setViolationImageKey(finalKey);
        ManualViolation saved = manualViolationRepo.save(violation);

        return new CreateViolationResponse(true, saved.getId());
    }

    private @NonNull User getUser(AppUserDetails userDetails) {
        User user = userRepo.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("user doesnt exists"));
        return user;
    }

    public List<GetViolationResponse> getViolations(AppUserDetails userDetails) {
        User user = getUser(userDetails);
        String userRole = user.getRole().getCode();

        if (userRole.equals(UserRole.INSPECTOR.name())) {
            log.info("getViolations - Inspector branch ");
            List<GetViolationResponse> response = manualViolationRepo.findByInspectorId(userDetails.getId()).stream().map(GetViolationResponse::from).toList();
            log.info("Current Inspector Id: {}", user.getId());
            response.forEach(v -> log.info("Violation Inspector id: {}", v.inspectorId()));
            log.info("user={} id={} role={}", userDetails.getUsername(), userDetails.getId(), userRole);
            return response;
        }
        if (userRole.equals(UserRole.OPERATOR.name())) {
            log.info("getViolations - operator branch ");
            return violationRepo.findByCurrentAssignmentOperatorId(userDetails.getId()).stream().map(GetViolationResponse::from).toList();
        }

        log.info("getViolations - default branch ");
        return violationRepo.findAll().stream().map(GetViolationResponse::from).toList();
    }

    @Transactional
    public boolean claimViolations(AppUserDetails userDetails) {
        assignViolations(userDetails.getId());
        return true;
    }

    private boolean assignViolations(Long operatorId) {
        Instant newInstant = Instant.now();
        // Get Violations assigned to operator with status NEW
        List<Violation> violations = violationRepo.findByCurrentAssignmentOperatorIdAndStatusId(operatorId, Status.from(StatusCode.ASSIGNED).getId());
        log.info("Currently Assigned Cases: {}", violations.stream().count());
        log.info("Currently Assigned Cases: {}", violations);

        // Get Violations unassigned cases
        Optional<Violation> optionalViolation = violationRepo.findByCurrentAssignmentOperatorId(null).stream().findFirst();
        // Optional<Violation> findFirstByCurrentAssignmentIsNullAndStatusIdOrderByViolationDateAsc(Long statusId);

        if (optionalViolation.isEmpty()) {
            log.info("No Unassigned Cases");
            return false;
        }
        Violation violation = optionalViolation.get();
        // Assigning the violation
        if (violation.getCurrentAssignment() != null) {
            updateAssignmentHistory(violation.getCurrentAssignment().getId(), Outcome.RELEASED, newInstant);
            violation.setCurrentAssignment(null);
            violationRepo.save(violation);
        }
        violation.setStatus(Status.from(StatusCode.ASSIGNED));

        Violation savedViolation = violationRepo.save(violation);
        ViolationAssignmentHistory history = addNewAssignmentHistory(savedViolation, userRepo.findById(operatorId).orElseThrow(), newInstant, StatusCode.ASSIGNED);
        savedViolation.setCurrentAssignment(history);
        Violation saved2Violation = violationRepo.save(savedViolation);

        // removing other violation assignment
        if (!violations.isEmpty()) {
            violations.forEach(oldViolation -> {
                ViolationAssignmentHistory assignmentHistory = oldViolation.getCurrentAssignment();
                updateAssignmentHistory(assignmentHistory.getId(), Outcome.RELEASED, newInstant);
                oldViolation.setCurrentAssignment(null);
                violationRepo.save(oldViolation);
                /* Optional<ViolationAssignmentHistory> assignmentHistory = assignmentHistoryRepo.findByViolationIdAndOperatorIdAndOutcomeIsNull(oldViolation.getId(), operatorId);
                if (assignmentHistory.isPresent()) {
                    updateAssignmentHistory(assignmentHistory.get().getId(), StatusCode.NEW, newInstant);
                } else {
                    log.warn("Violation with id: {}, was assigned but no assignment history");
                }*/
            });
        }
        log.info("Violation Assigned Successfully ");
        return true;
    }

    public GetViolationResponse getViolation(AppUserDetails userDetails, String violationId) {
        /**
         * NOTE: going with multiple queries for each case will make the result less
         * but going with one query will make the Exception more accurate
         * e.g. FORBIDDEN for violations existing but not `created by inspector` or `assigned to operator`
         */

        Long vId = Long.valueOf(violationId);
        User user = userRepo.findUserByUsername(userDetails.getUsername()).orElseThrow();
        String userRole = user.getRole().getCode();

        if (userRole.equals(UserRole.INSPECTOR.name())) {
            log.info("getViolations - Inspector branch ");
            log.info("violation ID: {}", violationId);
            log.info("VID: {}", vId);
            log.info("USERID: {}", userDetails.getId());
            Violation violation = manualViolationRepo.findByIdAndInspectorId(vId, userDetails.getId());
            if (violation == null) {
                throw new BadRequestException("Violation not found");
            }
            GetViolationResponse response = GetViolationResponse.from(violation);
            return response;
        }
        if (userRole.equals(UserRole.OPERATOR.name())) {
            log.info("getViolations - operator branch ");
            Optional<Violation> violation = violationRepo.findByIdAndCurrentAssignmentOperatorId(vId, userDetails.getId());
            if (violation.isEmpty()) {
                throw new BadRequestException("Violation not found");
            }
            return GetViolationResponse.from(violation.get());
        }

        log.info("getViolations - default branch ");
        return GetViolationResponse.from(violationRepo.findById(vId).get());
    }

    public Violation getViolation(String violationId) {
        Long vId = Long.valueOf(violationId);
        return violationRepo.findById(vId).get();

    }

    public Null acceptViolation(String id, AppUserDetails userDetails) {
        // todo: fix
        Violation violation = getViolation(id);
        log.info("Violation: {}", violation);
        User user = getUser(userDetails);
        Role role = user.getRole();
        Instant instant = Instant.now();

        if (!UserRole.OPERATOR.name().equals(role.getCode())) {
            log.error("User Role: {}, Processing violation allowed only for: {}", role.getCode(), UserRole.OPERATOR.name());
            throw new ForbiddenException("You don't have the privilege to process violations");
        }
        ViolationAssignmentHistory history = violation.getCurrentAssignment();
        if (history == null) {
            throw new RuntimeException("some error");
        }

        User assignedOperator = history.getOperator();

        if (assignedOperator == null || !Objects.equals(assignedOperator.getId(), user.getId())) {
            log.warn("User {} tried to process violation {} assigned to {}",
                    user.getId(), violation.getId(),
                    assignedOperator != null ? assignedOperator.getId() : null);
            throw new ForbiddenException("This violation is not assigned to you");
        }
        if (!StatusCode.NEW.name().equals(violation.getStatus().getCode())) {
            throw new ForbiddenException("Violation Can't be proccessed");
        }
        Status approved = statusRepo.findByCode(StatusCode.APPROVED.name())
                .orElseThrow(() -> new RuntimeException("APPROVED status not found"));
        violation.setStatus(approved);
        violationRepo.save(violation);
        updateAssignmentHistory(violation.getId(), Outcome.DECIDED, instant);
        return null;
    }

//    public void addAssignmentHistory(Violation v, User user, Instant date) {
//        // WIP
//        ViolationAssignmentHistory history = new ViolationAssignmentHistory();
//        history.setViolation(v);
//        history.setAssignedAt(date == null ? v.getAssignedAt().toInstant() : date);
//    }

    public ViolationAssignmentHistory addNewAssignmentHistory(Violation v, User user, Instant instant, StatusCode statusCode) {
        ViolationAssignmentHistory history = new ViolationAssignmentHistory();
        history.setViolation(v);
        history.setOperator(user);
        history.setAssignedAt(instant);
        return assignmentHistoryRepo.save(history);
    }

    public void updateAssignmentHistory(Long id, Outcome outcome, Instant instant) {
        ViolationAssignmentHistory history = assignmentHistoryRepo.getById(id);
        history.setEndedAt(instant);
        history.setOutcome(outcome.name());
        assignmentHistoryRepo.save(history);
    }

}
