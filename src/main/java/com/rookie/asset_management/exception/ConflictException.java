package com.rookie.asset_management.exception;

/**
 * Custom exception class for resource conflict errors.
 * This class is used for handling resource conflict errors in the application.
 * It is typically used in RESTful APIs to return a 409 Conflict status code.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
