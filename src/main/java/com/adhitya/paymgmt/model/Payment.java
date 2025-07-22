package com.adhitya.paymgmt.model;

import com.adhitya.paymgmt.model.enums.PaymentCategory;
import com.adhitya.paymgmt.model.enums.PaymentDirection;
import com.adhitya.paymgmt.model.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a payment transaction in the payment management system.
 * Encapsulates details such as amount, payment direction, category, status,
 * associated entities (employee or counterparty), and audit information.
 */
public class Payment {
  // Unique identifier for the payment record
  private int id;

  // Monetary amount involved in the payment
  private BigDecimal amount;

  // Direction of the payment (e.g., incoming or outgoing)
  private PaymentDirection paymentDirection;

  // Category of the payment (e.g., salary, expense)
  private PaymentCategory category;

  // Current status of the payment (e.g., pending, completed)
  private Status status;

  // Additional textual description or notes for the payment
  private String description;

  // User who created the payment record, for auditing purposes
  private User createdBy;

  // Timestamp when the payment record was created
  private LocalDateTime createdAt;

  // Timestamp when the payment record was last updated
  private LocalDateTime updatedAt;

  // Employee associated with the payment, if applicable
  private Employee employee;

  // Counterparty associated with the payment, if applicable
  private Counterparty counterParty;


  // Default no-argument constructor
  public Payment() {};

  /**
   * Parameterized constructor for creating a fully populated Payment instance.
   *
   * @param id unique payment ID
   * @param amount payment amount
   * @param paymentDirection direction of payment (incoming/outgoing)
   * @param category payment category (salary, vendor, etc.)
   * @param status current status of the payment
   * @param description notes or comments for the payment
   * @param createdBy user who created the payment record
   * @param createdAt creation timestamp
   * @param updatedAt last update timestamp
   * @param employee associated employee (nullable)
   * @param counterParty associated counterparty (nullable)
   */
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

  // Getter and setter methods follow for accessing and modifying private fields

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
