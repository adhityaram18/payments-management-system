package com.adhitya.paymgmt.repository;

import com.adhitya.paymgmt.config.DatabaseConfig;
import com.adhitya.paymgmt.exception.DataAccessException;
import com.adhitya.paymgmt.model.Employee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
  private static final Logger logger = LoggerFactory.getLogger(EmployeeRepository.class);

  public Employee findById(int id) {
    String sql = "SELECT id, name, department, created_at FROM employees WHERE id = ?";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,id);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          Timestamp createdAtTs = resultSet.getTimestamp("created_at");
          LocalDateTime createdAt = (createdAtTs != null) ? createdAtTs.toLocalDateTime() : null;

          return new Employee(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("department"),
            createdAt
          );
        }
      }
    }
    catch (SQLException ex) {
      logger.error("Failed to retrieve employee", ex);
      throw new DataAccessException("Database error while fetching employee", ex);
    }

    return null;
  }

  public List<Employee> findAll() {
    String sql = "SELECT id, name, department, created_at FROM employees";
    List<Employee> employeeList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery();
    ) {

      while(resultSet.next()) {
        Timestamp createdAtTs = resultSet.getTimestamp("created_at");
        LocalDateTime createdAt = (createdAtTs != null) ? createdAtTs.toLocalDateTime() : null;

        employeeList.add(new Employee(
          resultSet.getInt("id"),
          resultSet.getString("name"),
          resultSet.getString("department"),
          createdAt
        ));
      }
    }
    catch (SQLException ex) {
      logger.error("Failed to retrieve employees", ex);
      throw new DataAccessException("Database error while fetching employees", ex);
    }

    return employeeList;
  }

  public void save(Employee employee) {

    if (employee.getName() == null || employee.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null/empty");
    }

    String sql = "INSERT INTO employees (name, department, created_at) VALUES (?, ?, ?)";

    try(Connection connection = DatabaseConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    ) {
      preparedStatement.setString(1,employee.getName());
      preparedStatement.setString(2, employee.getDepartment() != null ? employee.getDepartment() : null);
      preparedStatement.setTimestamp(3, employee.getCreatedAt() != null ? Timestamp.valueOf(employee.getCreatedAt()) : null);

      int affectedRows = preparedStatement.executeUpdate();

      if (affectedRows == 0) {
        throw new DataAccessException("Failed to save employee: 0 rows affected");
      }

      try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
        if (rs.next()) {
          employee.setId(rs.getInt(1));
        }
      }
    }
    catch (SQLException ex) {
      logger.error("Error saving employee: " + employee.getName(), ex);
      throw new DataAccessException("Failed to save employee", ex);
    }
  }
}
