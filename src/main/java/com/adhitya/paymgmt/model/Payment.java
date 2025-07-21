package com.adhitya.paymgmt.model;

import com.adhitya.paymgmt.model.enums.PaymentCategory;
import com.adhitya.paymgmt.model.enums.PaymentDirection;
import com.adhitya.paymgmt.model.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
  private int id;
  private BigDecimal amount;
  private PaymentDirection paymentDirection;
  private PaymentCategory category;
  private Status status;
  private String description;
  private User createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Employee employee;
  private Counterparty counterParty;


  public Payment() {};

  public Payment(int id, BigDecimal amount, PaymentDirection paymentDirection, PaymentCategory category, Status status, String description, User createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, Employee employee, Counterparty counterParty) {
    this.id = id;
    this.amount = amount;
    this.paymentDirection = paymentDirection;
    this.category = category;
    this.status = status;
    this.description = description;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.employee = employee;
    this.counterParty = counterParty;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public PaymentDirection getPaymentDirection() {
    return paymentDirection;
  }

  public void setPaymentDirection(PaymentDirection paymentDirection) {
    this.paymentDirection = paymentDirection;
  }

  public PaymentCategory getCategory() {
    return category;
  }

  public void setCategory(PaymentCategory category) {
    this.category = category;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public Counterparty getCounterParty() {
    return counterParty;
  }

  public void setCounterParty(Counterparty counterParty) {
    this.counterParty = counterParty;
  }
}
