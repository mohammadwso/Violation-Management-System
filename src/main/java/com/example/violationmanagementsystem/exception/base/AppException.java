package com.example.violationmanagementsystem.exception.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public abstract class AppException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public AppException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public AppException(String message, Throwable cause, String errorCode, HttpStatus status) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }

}
