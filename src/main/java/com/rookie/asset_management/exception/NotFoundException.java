package com.rookie.asset_management.exception;

/**
 * Exception thrown when a resource is not found. This exception is used to indicate that a
 * requested resource could not be found. It is typically used in RESTful APIs to return a 404 Not
 * Found status code.
 */
public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
