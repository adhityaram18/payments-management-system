package com.adhitya.paymgmt.model.enums;

public enum PaymentDirection {
  INCOMING("INCOMING"),
  OUTGOING("OUTGOING");

  private final String dbValue;

  PaymentDirection(String dbValue) {
    this.dbValue = dbValue;
  }

  public String getDbValue() {
    return dbValue;
  }

  public static PaymentDirection fromDbValue(String dbValue) {
    for (PaymentDirection direction : values()) {
      if (direction.dbValue.equalsIgnoreCase(dbValue)) {
        return direction;
      }
    }
    throw new IllegalArgumentException("Invalid PaymentDirection: " + dbValue);
  }
}
