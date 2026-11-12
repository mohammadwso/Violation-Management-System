package com.example.violationmanagementsystem.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public interface StorageService {

    // setting
    public String upload(String imageKey, InputStream image, Long size, String contentType);

    public boolean exists(String imageKey);

    public boolean promote(String imageKey);

    // Getting
    public InputStream download(String imageKey);

    public boolean delete(String imageKey);

}
