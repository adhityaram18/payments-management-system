package com.adhitya.paymgmt.exception;

/** Thrown when user authentication fails due to invalid credentials. */
public class InvalidCredentialsException extends RuntimeException {

  /** Creates exception with error message. */
  public InvalidCredentialsException(String message) {
    super(message);
  }

  /** Creates exception with message and cause. */
  public InvalidCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }
}