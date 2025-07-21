package com.adhitya.paymgmt.model.enums;

public enum Status {
  PENDING("PENDING"),
  PROCESSING("PROCESSING"),
  COMPLETED("COMPLETED");

  private final String dbValue;

  Status(String dbValue) {
    this.dbValue = dbValue;
  }

  public String getDbValue() {
    return dbValue;
  }

  public static Status fromDbValue(String dbValue) {
    for (Status status : values()) {
      if (status.dbValue.equalsIgnoreCase(dbValue)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Invalid Status: " + dbValue);
  }
}
