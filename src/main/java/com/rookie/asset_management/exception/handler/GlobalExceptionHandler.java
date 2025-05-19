package com.rookie.asset_management.exception.handler;

import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for handling exceptions in the application.
 * This class uses Spring's @RestControllerAdvice annotation to handle exceptions
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // handle defined exceptions
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleAppException(AppException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder()
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(ex.getHttpStatusCode()).body(response);
  }

  // handle case when spring throws NoResourceFoundException when no handler is found
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder()
        .message("Resource not found")
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(404).body(response);
  }

  // handle when other exceptions are thrown without any specific handler
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleException(Exception ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder()
        .message("Internal server error")
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(500).body(response);
  }
}
