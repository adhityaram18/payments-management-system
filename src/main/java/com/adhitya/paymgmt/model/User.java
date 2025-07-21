package com.adhitya.paymgmt.model;

import com.adhitya.paymgmt.model.enums.Role;

import java.time.LocalDateTime;

public class User {
  private int id;
  private String username;
  private String password; // hashed
  private Role role;
  private String email;
  private LocalDateTime createdAt;

  public User() {};

  public User(int id, String username, String password, Role role, String email, LocalDateTime createdAt) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
    this.email = email;
    this.createdAt = createdAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
