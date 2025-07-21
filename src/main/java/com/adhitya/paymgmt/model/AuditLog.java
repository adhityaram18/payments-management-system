package com.adhitya.paymgmt.model;

import java.time.LocalDateTime;

public class AuditLog {
  private int id;
  private Payment payment;
  private String fieldChanged;
  private String oldValue;
  private String newValue;
  private User changedBy;
  private LocalDateTime changedAt;

  public AuditLog() {};

  public AuditLog(int id, Payment payment, String fieldChanged, String oldValue, String newValue, User changedBy, LocalDateTime changedAt) {
    this.id = id;
    this.payment = payment;
    this.fieldChanged = fieldChanged;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.changedBy = changedBy;
    this.changedAt = changedAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Payment getPayment() {
    return payment;
  }

  public void setPayment(Payment payment) {
    this.payment = payment;
  }

  public String getFieldChanged() {
    return fieldChanged;
  }

  public void setFieldChanged(String fieldChanged) {
    this.fieldChanged = fieldChanged;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  public User getChangedBy() {
    return changedBy;
  }

  public void setChangedBy(User changedBy) {
    this.changedBy = changedBy;
  }

  public LocalDateTime getChangedAt() {
    return changedAt;
  }

  public void setChangedAt(LocalDateTime changedAt) {
    this.changedAt = changedAt;
  }
}
