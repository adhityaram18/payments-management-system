package com.adhitya.paymgmt.repository;

import com.adhitya.paymgmt.config.DatabaseConfig;
import com.adhitya.paymgmt.exception.DataAccessException;
import com.adhitya.paymgmt.model.User;
import com.adhitya.paymgmt.model.enums.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.adhitya.paymgmt.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository {
  private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

  public User findById(int id) {
    String sql = "SELECT id, username, password, role, email, created_at FROM users WHERE id = ?";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,id);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          Timestamp createdAtTs = resultSet.getTimestamp("created_at");
          return new User(
            resultSet.getInt("id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            Role.valueOf(resultSet.getString("role")),
            resultSet.getString("email"),
            createdAtTs != null ? createdAtTs.toLocalDateTime() : null
          );
        }

      }
    }
    catch (SQLException ex) {
      logger.error("Error finding user by id: " + id, ex);
      throw new DataAccessException("Failed to find user", ex);
    }

    return null;
  }

  public User findByUsername(String username) {
    String sql = "SELECT id, username, password, role, email, created_at FROM users WHERE username = ?";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ) {

      preparedStatement.setString(1, username);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          Timestamp createdAtTs = resultSet.getTimestamp("created_at");
          return new User(
            resultSet.getInt("id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            Role.valueOf(resultSet.getString("role")),
            resultSet.getString("email"),
            createdAtTs != null ? createdAtTs.toLocalDateTime() : null
          );
        }
      }

    }
    catch (SQLException ex) {
      logger.error("Error finding user by username: " + username, ex);
      throw new DataAccessException("Failed to find user", ex);
    }

    return null;
  }

  public List<User> findAll() {
    String sql = "SELECT id, username, password, role, email, created_at FROM users";
    List<User> userList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery()) {

      while (resultSet.next()) {
        Timestamp createdAtTs = resultSet.getTimestamp("created_at");
        userList.add(new User(
          resultSet.getInt("id"),
          resultSet.getString("username"),
          resultSet.getString("password"),
          Role.valueOf(resultSet.getString("role")),
          resultSet.getString("email"),
          createdAtTs != null ? createdAtTs.toLocalDateTime() : null
        ));
      }
    }
    catch (SQLException ex) {
      logger.error("Failed to retrieve users", ex);
      throw new DataAccessException("Database error while fetching users", ex);
    }

    return userList;
  }

  public void save(User user) {

    if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null/empty");
    }
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be null/empty");
    }
    if (user.getRole() == null) {
      throw new IllegalArgumentException("Role cannot be null");
    }

    String sql = "INSERT INTO users (username, password, role, email, created_at) VALUES (?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      preparedStatement.setString(1, user.getUsername());
      preparedStatement.setString(2, PasswordUtil.hashPassword(user.getPassword()));
      preparedStatement.setString(3, user.getRole().name());
      preparedStatement.setString(4, user.getEmail());
      preparedStatement.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));

      if (preparedStatement.executeUpdate() == 0) {
        throw new DataAccessException("Failed to save user: 0 rows affected");
      }

      try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
        if (rs.next()) {
          user.setId(rs.getInt(1));
        }
      }

    } catch (SQLException ex) {
      if (ex.getMessage().contains("unique constraint")) {
        throw new DataAccessException("Username already exists: " + user.getUsername(), ex);
      }
      logger.error("Failed to save user: {}", user.getUsername(), ex);
      throw new DataAccessException("Database error while saving user", ex);
    }
  }
}
