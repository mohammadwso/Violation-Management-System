package com.example.violationmanagementsystem.service;

import com.example.violationmanagementsystem.config.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3StorageService implements StorageService {

    @Autowired
    S3Client s3Client;

    @Autowired
    StorageProperties storageProperties;

    @Override
    public String createStagingKey(String sourceCode) {
        return storageProperties.stagingKey() + '/' + sourceCode.toLowerCase() + "/" + UUID.randomUUID();
    }

    @Override
    public String createViolationsKey(String sourceCode) {
        return storageProperties.violationsKey() + '/' + sourceCode.toLowerCase() + "/" + UUID.randomUUID();
    }

    @Override
    public String upload(String imageKey, MultipartFile image, Long size, String contentType) throws IOException {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(storageProperties.bucket())
                .key(imageKey)
                .contentType(contentType)
                .contentLength(size)
                .build();

        PutObjectResponse response = s3Client.putObject(objectRequest, RequestBody.fromBytes(image.getBytes()));
        // If the upload failed it would throw an error
        System.out.println(response);
        return imageKey;
    }

    @Override
    public boolean exists(String imageKey) {
        // not existing will throw an error, but we don't need to throw it as the function is for checking only
        try {
            s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(storageProperties.bucket())
                            .key(imageKey)
                            .build()
            );

            return true;

        } catch (S3Exception e) {
            return false;
        }
    }

    @Override
    public String promote(String imageKey) {
        String finalKey = imageKey.replace("staging", "violations");
        CopyObjectResponse response = s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(storageProperties.bucket())
                .sourceKey(imageKey)
                .destinationBucket(storageProperties.bucket())
                .destinationKey(finalKey)
                .build()
        );

        System.out.println(response);
        return finalKey;
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
