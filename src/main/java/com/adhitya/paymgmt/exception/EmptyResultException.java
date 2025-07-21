package com.adhitya.paymgmt.exception;

public class EmptyResultException extends RuntimeException {
  public EmptyResultException(String message) {
    super(message);
  }

  public EmptyResultException(String message, Throwable cause) {
    super(message, cause);
  }
}
