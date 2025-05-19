package com.rookie.asset_management.exception;

/**
 * Custom exception class for unauthorized access errors.
 * This class is used for handling unauthorized access errors in the application.
 * It is typically used in RESTful APIs to return a 401 UnAuthorized status code.
 */
public class UnAuthorizedException extends RuntimeException {
  public UnAuthorizedException(String message) {
    super(message);
  }
}
