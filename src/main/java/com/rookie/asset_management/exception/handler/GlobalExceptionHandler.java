package com.rookie.asset_management.exception.handler;

import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for handling exceptions in the application. This class uses
 * Spring's @RestControllerAdvice annotation to handle exceptions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  // handle defined exceptions
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleAppException(AppException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder().message(ex.getMessage()).build();
    return ResponseEntity.status(ex.getHttpStatusCode()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiDtoResponse<String>> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .orElse("Validation error");
    ApiDtoResponse<String> response =
        ApiDtoResponse.<String>builder().message(errorMessage).build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // handle case when spring throws MissingServletRequestParameterException when a required
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex) {
    ApiDtoResponse<Void> response =
        ApiDtoResponse.<Void>builder()
            .message(String.format("Missing request parameter: %s", ex.getParameterName()))
            .build();
    return ResponseEntity.status(400).body(response);
  }

  // handle case when spring throws NoResourceFoundException when no handler is found
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleNoResourceFoundException(
      NoResourceFoundException ex) {
    ApiDtoResponse<Void> response =
        ApiDtoResponse.<Void>builder().message("Resource not found").build();
    return ResponseEntity.status(404).body(response);
  }

  // handle when other exceptions are thrown without any specific handler
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleException(Exception ex) {
    ApiDtoResponse<Void> response =
        ApiDtoResponse.<Void>builder().message("Internal server error").build();
    return ResponseEntity.status(500).body(response);
  }

  // handle when other exception are thrown invalid request
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handIllegalArgumentException(
      IllegalArgumentException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder().message(ex.getMessage()).build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // handle exception for password encoder
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiDtoResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
    ApiDtoResponse<Void> response = ApiDtoResponse.<Void>builder().message(ex.getMessage()).build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }
}
