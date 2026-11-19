package com.example.violationmanagementsystem.exception;

import com.example.violationmanagementsystem.exception.base.AppException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppException {
    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
}
