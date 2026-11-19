package com.example.violationmanagementsystem.exception;

import com.example.violationmanagementsystem.dto.response.ErrorResponse;
import com.example.violationmanagementsystem.exception.base.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(message, ErrorCode.DATA_CONFLICT, HttpStatus.CONFLICT);
    }
}
