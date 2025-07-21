package com.adhitya.paymgmt.model.enums;

public enum PaymentCategory {
  SALARY("SALARY"),
  VENDOR_PAYMENT("VENDOR_PAYMENT"),
  CLIENT_INVOICE("CLIENT_INVOICE");

  private final String dbValue;

  PaymentCategory(String dbValue) {
    this.dbValue = dbValue;
  }

  public String getDbValue() {
    return dbValue;
  }

  public static PaymentCategory fromDbValue(String dbValue) {
    for (PaymentCategory c : values()) {
      if (c.dbValue.equalsIgnoreCase(dbValue)) return c;
    }
    throw new IllegalArgumentException("Invalid category: " + dbValue);
  }
}

