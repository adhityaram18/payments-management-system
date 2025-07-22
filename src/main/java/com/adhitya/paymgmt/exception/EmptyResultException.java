package com.adhitya.paymgmt.exception;

/** Thrown when a query returns no results. */
public class EmptyResultException extends RuntimeException {

  /** Creates exception with error message. */
  public EmptyResultException(String message) {
    super(message);
  }

  /** Creates exception with message and cause. */
  public EmptyResultException(String message, Throwable cause) {
    super(message, cause);
  }
}