package com.adhitya.paymgmt.service;

import com.adhitya.paymgmt.exception.EmptyResultException;
import com.adhitya.paymgmt.exception.InvalidCredentialsException;
import com.adhitya.paymgmt.exception.UserNotFoundException;
import com.adhitya.paymgmt.model.User;
import com.adhitya.paymgmt.repository.UserRepository;
import com.adhitya.paymgmt.util.PasswordUtil;

import java.util.List;

public class AuthService {
  private final UserRepository userRepository;

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User login(String userName, String password) {
    if (userName == null || userName.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (password == null || password.isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }

    User user = userRepository.findByUsername(userName);

    if(user == null) {
      throw new UserNotFoundException("Invalid Credentials");
    }
    if(!PasswordUtil.verifyPassword(password,user.getPassword())) {
      throw new InvalidCredentialsException("Invalid Credentials");
    }

    return user;
  }

  public void register(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    userRepository.save(user);
  }

  public User findById(int id) {
    if (id <= 0) {
      throw new IllegalArgumentException("Invalid user ID: " + id);
    }

    User user = userRepository.findById(id);

    if (user == null) {
      throw new UserNotFoundException("User not found with ID: " + id);
    }

    return user;
  }


  public List<User> getAllUsers() {
    List<User> users = userRepository.findAll();

    if (users.isEmpty()) {
      throw new EmptyResultException("No users found");
    }

    return users;
  }
}
