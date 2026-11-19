package com.example.violationmanagementsystem.dto.response;

import com.example.violationmanagementsystem.enums.ResponseStatusCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    Integer status;
    String statusMessage;
    T data;
    List<MessageResponse> errors;
    MessageResponse message;

    // NO DATA RETURN
    public static <T> ResponseEntity<ApiResponse<T>> getSuccessResponse(MessageResponse message) {
        return getResponse(ResponseStatusCode.SUCCESS, Collections.emptyList(), HttpStatus.OK, message);
    }

    //DATA ONLY
    public static <T> ResponseEntity<ApiResponse<T>> getSuccessResponse(T data) {
        return getResponse(ResponseStatusCode.SUCCESS, data, Collections.emptyList(), HttpStatus.OK, null);
    }

    // BOTH
    public static <T> ResponseEntity<ApiResponse<T>> getSuccessResponse(T data, MessageResponse message) {
        return getResponse(ResponseStatusCode.SUCCESS, data, Collections.emptyList(), HttpStatus.OK, message);
    }

    // NO DATA RETURN
    private static <T> ResponseEntity<ApiResponse<T>> getResponse(ResponseStatusCode responseStatusCode, List<Object> objects, HttpStatus httpStatus, MessageResponse message) {
        return getResponse(responseStatusCode, null, Collections.emptyList(), httpStatus, message);
    }

    private static <T> ResponseEntity<ApiResponse<T>> getResponse(
            ResponseStatusCode statusCode,
            T data,
            List<MessageResponse> errors,
            HttpStatus httpStatus,
            MessageResponse message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(statusCode.getId());
        response.setStatusMessage(statusCode.getMessage());
        response.setData(data);
        response.setErrors(errors);
        response.setMessage(message);

        return new ResponseEntity<>(response, httpStatus);
    }
}
