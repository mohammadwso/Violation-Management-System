package com.example.violationmanagementsystem.service;

import com.example.violationmanagementsystem.entity.Region;
import com.example.violationmanagementsystem.entity.Source;
import com.example.violationmanagementsystem.entity.ViolationType;
import com.example.violationmanagementsystem.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LookupService {
    // TODO: DTOs
    @Autowired
    private RegionRepo regionRepo;
    @Autowired
    private SourceRepo sourceRepo;
    // @Autowired
    // private StatusRepo statusRepo;
    @Autowired
    private ViolationTypeRepo violationTypeRepo;
    @Autowired
    private RoleRepo roleRepo;

    public List<Region> getRegions() {
        return regionRepo.findAll();
    }

    public List<ViolationType> getViolationTypes() {
        return violationTypeRepo.findAll();
    }
}
