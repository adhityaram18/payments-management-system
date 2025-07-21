package com.adhitya.paymgmt.model.enums;

public enum Role {
  ADMIN("ADMIN"),
  FINANCE_MANAGER("FINANCE_MANAGER"),
  VIEWER("VIEWER");

  private final String dbValue;

  Role(String dbValue) {
    this.dbValue = dbValue;
  }

  public String getDbValue() {
    return dbValue;
  }

  public static Role fromDbValue(String dbValue) {
    for (Role role : values()) {
      if (role.dbValue.equalsIgnoreCase(dbValue)) {
        return role;
      }
    }
    throw new IllegalArgumentException("Invalid Role: " + dbValue);
  }
}
