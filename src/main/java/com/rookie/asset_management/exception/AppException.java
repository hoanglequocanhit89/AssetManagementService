package com.rookie.asset_management.exception;

import lombok.Getter;

import org.springframework.http.HttpStatus;

/**
 * Custom exception class for application-specific errors. This class is used for handling
 * application-specific errors in the application.
 */
@Getter
public class AppException extends RuntimeException {
  private final HttpStatus httpStatusCode;

  public AppException(HttpStatus httpStatusCode, String message) {
    super(message);
    this.httpStatusCode = httpStatusCode;
  }
}
