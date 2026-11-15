package com.example.violationmanagementsystem.controller;

import com.example.violationmanagementsystem.entity.Region;
import com.example.violationmanagementsystem.entity.ViolationType;
import com.example.violationmanagementsystem.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/lookup")
@RestController
public class LookupController {

    @Autowired
    LookupService lookupService;

    @GetMapping("/regions")
    public List<Region> getRegions() {
        return lookupService.getRegions();
    }
    @GetMapping("/violation-types")
    public List<ViolationType> getViolationTypes() {
        return lookupService.getViolationTypes();
    }
}
