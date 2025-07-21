package com.adhitya.paymgmt.model;

import java.time.LocalDateTime;

public class Employee {
  private int id;
  private String name;
  private String department;
  private LocalDateTime createdAt;

  public Employee() {};

  public Employee(int id, String name, String department, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.department = department;
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

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
