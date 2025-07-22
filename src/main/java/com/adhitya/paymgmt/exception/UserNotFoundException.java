package com.adhitya.paymgmt.exception;

/** Thrown when a requested user cannot be found. */
public class UserNotFoundException extends RuntimeException {

  /** Creates exception with error message. */
  public UserNotFoundException(String message) {
    super(message);
  }

  /** Creates exception with message and cause. */
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}