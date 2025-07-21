package com.adhitya.paymgmt.model.enums;

public enum PartyType {
  VENDOR("VENDOR"),
  CLIENT("CLIENT");

  private final String dbValue;

  PartyType(String dbValue) {
    this.dbValue = dbValue;
  }

  public String getDbValue() {
    return dbValue;
  }

  public static PartyType fromDbValue(String dbValue) {
    for (PartyType partyType : values()) {
      if (partyType.dbValue.equalsIgnoreCase(dbValue)) {
        return partyType;
      }
    }
    throw new IllegalArgumentException("Invalid PartyType: " + dbValue);
  }
}
