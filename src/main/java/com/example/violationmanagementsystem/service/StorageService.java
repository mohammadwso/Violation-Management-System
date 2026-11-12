package com.example.violationmanagementsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public interface StorageService {

    // setting
    public String upload(String imageKey, MultipartFile image, Long size, String contentType) throws IOException;

    public boolean exists(String imageKey);

    public boolean promote(String imageKey);

    // Getting
    public InputStream download(String imageKey);

    public boolean delete(String imageKey);

}
