package com.example.violationmanagementsystem.handler;

import com.example.violationmanagementsystem.dto.response.ErrorResponse;
import com.example.violationmanagementsystem.exception.BadRequestException;
import com.example.violationmanagementsystem.exception.ConflictException;
import com.example.violationmanagementsystem.exception.ErrorCode;
import com.example.violationmanagementsystem.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_TYPE_BASE = "https://vms.example.com/errors/";

    @Override
//    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

        ProblemDetail problemDetail = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                ErrorCode.VALIDATION_FAILED,
                (HttpServletRequest) request,
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        ProblemDetail problemDetail = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                ErrorCode.BAD_REQUEST,
                req,
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ConflictException ex, HttpServletRequest req) {
        ProblemDetail problemDetail = buildProblemDetail(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                ErrorCode.DATA_CONFLICT,
                req,
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildProblemDetail(HttpStatus.FORBIDDEN, ex.getMessage(), ErrorCode.FORBIDDEN, req, null));
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String message, String errorCode,
                                             HttpServletRequest req, Map<String, String> fieldErrors) {
        log.error("Status: {}", status);
        log.error("Message: {}", message);
        log.error("Request: {}", req);
        log.error("Fields: {}", fieldErrors);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setType(URI.create(ERROR_TYPE_BASE + errorCode.toLowerCase().replace("_", "-")));
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setInstance(URI.create(req.getRequestURI()));
        problemDetail.setProperty("errorCode", errorCode);
        problemDetail.setProperty("traceId", resolveTraceId());
        problemDetail.setProperty("timeStamp", Instant.now());
        problemDetail.setProperty("fieldErrors", fieldErrors);

        return problemDetail;
    }

    private String resolveTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null ? traceId : UUID.randomUUID().toString();
    }
}