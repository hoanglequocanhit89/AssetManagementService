package com.rookie.asset_management.exception;

/**
 * Exception thrown when attempting to create a category with a name or prefix
 * that already exists in the system.
 *
 * <p>This exception is typically used to enforce the uniqueness constraint
 * on category names and prefixes during category creation.
 */
public class DuplicateCategoryException extends RuntimeException {
  public DuplicateCategoryException(String message) {
    super(message);
  }
}
