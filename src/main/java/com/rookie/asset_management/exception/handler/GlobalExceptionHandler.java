package com.rookie.asset_management.exception.handler;

import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.exception.AccessDeniedException;
import com.rookie.asset_management.exception.ConflictException;
import com.rookie.asset_management.exception.NotFoundException;
import com.rookie.asset_management.exception.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling exceptions in the application.
 * This class uses Spring's @RestControllerAdvice annotation to handle exceptions
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // handle when the user is not authorized
  @ExceptionHandler(UnAuthorizedException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleUnAuthorizedException(UnAuthorizedException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder()
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(401).body(response);
  }

  // handle when the user is authorized but not allowed to access the resource
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder()
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(403).body(response);
  }

  // handle when the resource is not found
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleResourceNotFoundException(NotFoundException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder()
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(404).body(response);
  }

  // handle when the resource is already exists, delete a resource with can not be deleted, etc.
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleResourceConflictException(ConflictException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder()
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(409).body(response);
  }

  // handle when the user input is invalid
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder()
        .message(ex.getMessage())
        .build();
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(400).body(response);
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
