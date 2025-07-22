package com.adhitya.paymgmt.model;

import com.adhitya.paymgmt.model.enums.Role;

import java.time.LocalDateTime;

/**
 * Represents a user in the payment management system.
 * Contains authentication credentials, role-based authorization details,
 * and basic contact information along with account creation timestamp.
 */
public class User {
  // Unique identifier for the user
  private int id;

  // Username used for login and identification
  private String username;

  // Hashed password for authentication (never store plaintext)
  private String password;

  // Role of the user defining permissions and access control
  private Role role;

  // User's email address for communication and notifications
  private String email;

  // Timestamp when the user account was created
  private LocalDateTime createdAt;

  // Default no-argument constructor
  public User() {};

  /**
   * Parameterized constructor for creating a fully populated User instance.
   *
   * @param id unique user ID
   * @param username login name of the user
   * @param password hashed password for authentication
   * @param role role assigned to the user (e.g., ADMIN, USER)
   * @param email user's email address
   * @param createdAt account creation timestamp
   */
  public User(int id, String username, String password, Role role, String email, LocalDateTime createdAt) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
    this.email = email;
    this.createdAt = createdAt;
  }

  // Getter and setter methods for encapsulated fields

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
