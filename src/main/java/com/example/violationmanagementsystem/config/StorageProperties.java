package com.example.violationmanagementsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.s3")
public record StorageProperties(String endpoint, String bucket, String region, String accessKey, String secretKey) {

}
