package com.example.violationmanagementsystem.service;

import com.example.violationmanagementsystem.config.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;

@Service
public class S3StorageService implements StorageService{

    @Autowired
    S3Client s3Client;

    @Autowired
    StorageProperties storageProperties;

    @Override
    public String upload(String imageKey, InputStream image, Long size, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(storageProperties.bucket())
                .key(imageKey)
                .contentType(contentType)
                .contentLength(size)
                .build();
        PutObjectResponse response = s3Client.putObject(objectRequest, RequestBody.fromInputStream(image, size));
        System.out.println(response);
        return "";
    }

    @Override
    public boolean exists(String imageKey) {
        return false;
    }

    @Override
    public boolean promote(String imageKey) {
        return false;
    }

    @Override
    public InputStream download(String imageKey) {
        return null;
    }

    @Override
    public boolean delete(String imageKey) {
        return false;
    }
}
