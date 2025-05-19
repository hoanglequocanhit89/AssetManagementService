package com.rookie.asset_management.exception;

/**
 * Custom exception class for access denied errors.
 * This class uses for handling access denied errors in the application.
 * It is typically used in RESTful APIs to return a 403 forbidden status code.
 */
public class AccessDeniedException extends RuntimeException {
  public AccessDeniedException(String message) {
    super(message);
  }
}
