package com.adhitya.paymgmt.exception;

/**
 * Thrown to indicate a low-level data access error from the persistence layer,
 * typically triggered by failed database operations or unexpected database connectivity issues.
 */
public class DataAccessException extends RuntimeException {

  // Create a DataAccessException with a custom message describing the error.
  public DataAccessException(String message) {
    super(message);
  }

  // Create a DataAccessException with both a detailed message and a root cause.
  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
