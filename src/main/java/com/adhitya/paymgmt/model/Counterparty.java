package com.adhitya.paymgmt.model;

import com.adhitya.paymgmt.model.enums.PartyType;

import java.time.LocalDateTime;

public class Counterparty {
  private int id;
  private String name;
  private PartyType partyType;
  private String mobile;
  private LocalDateTime createdAt;

  public Counterparty() {};

  public Counterparty(int id, String name, PartyType partyType, String mobile, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.partyType = partyType;
    this.mobile = mobile;
    this.createdAt = createdAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PartyType getPartyType() {
    return partyType;
  }

  public void setPartyType(PartyType partyType) {
    this.partyType = partyType;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
