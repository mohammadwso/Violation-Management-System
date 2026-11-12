package com.example.violationmanagementsystem.service;

import com.example.violationmanagementsystem.dto.request.ViolationImageRequest;
import com.example.violationmanagementsystem.dto.response.ViolationImageResponse;
import com.example.violationmanagementsystem.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ViolationService {

    final String staging = "/staging";

    @Autowired
    StorageService storageService;

    public ViolationImageResponse storeImage(MultipartFile image, AppUserDetails userDetails) throws IOException {
        UUID uuid = UUID.randomUUID();
        String key = staging + '/' + userDetails.getUsername() + "/" + uuid.toString();
        String imageKey = this.storageService.upload(key, image, image.getSize(), image.getContentType());
        System.out.println(imageKey);
        if (imageKey != null) {
            return new ViolationImageResponse(true, imageKey);
        }
        return new ViolationImageResponse(false, null);
    }

}
