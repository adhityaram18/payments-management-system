package com.adhitya.paymgmt.repository;

import com.adhitya.paymgmt.config.DatabaseConfig;
import com.adhitya.paymgmt.exception.DataAccessException;
import com.adhitya.paymgmt.model.Payment;

import com.adhitya.paymgmt.model.enums.PaymentCategory;
import com.adhitya.paymgmt.model.enums.PaymentDirection;
import com.adhitya.paymgmt.model.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
  private static final Logger logger = LoggerFactory.getLogger(PaymentRepository.class);
  private final UserRepository userRepository;
  private final EmployeeRepository employeeRepository;
  private final CounterpartyRepository counterpartyRepository;

  public PaymentRepository(
    UserRepository userRepository,
    EmployeeRepository employeeRepository,
    CounterpartyRepository counterpartyRepository
  ) {
    this.userRepository = userRepository;
    this.employeeRepository = employeeRepository;
    this.counterpartyRepository = counterpartyRepository;
  }


  public Payment findById(int id) {
    String sql = "SELECT id, amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id " +
                 "FROM payments " +
                 "WHERE id = ?";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,id);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return new Payment(
            resultSet.getInt("id"),
            resultSet.getBigDecimal("amount"),
            PaymentDirection.valueOf(resultSet.getString("direction")),
            PaymentCategory.valueOf(resultSet.getString("category")),
            Status.valueOf(resultSet.getString("status")),
            resultSet.getString("description"),
            userRepository.findById(resultSet.getInt("created_by")),
            resultSet.getTimestamp("created_at").toLocalDateTime(),
            resultSet.getTimestamp("updated_at") != null ?
              resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
            employeeRepository.findById(resultSet.getInt("employee_id")),
            counterpartyRepository.findById(resultSet.getInt("counterparty_id"))
          );
        }
      }
    }
    catch (SQLException ex) {
      logger.error("Failed to retrieve payment", ex);
      throw new DataAccessException("Database error while fetching payment", ex);
    }

    return null;
  }

  public List<Payment> findAll() {
    String sql = "SELECT id, amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id " +
                 "FROM payments";
    List<Payment> paymentList = new ArrayList<>();

    try(Connection connection = DatabaseConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
    ) {
      while(resultSet.next()) {
        paymentList.add(new Payment(
          resultSet.getInt("id"),
          resultSet.getBigDecimal("amount"),
          PaymentDirection.valueOf(resultSet.getString("direction")),
          PaymentCategory.valueOf(resultSet.getString("category")),
          Status.valueOf(resultSet.getString("status")),
          resultSet.getString("description"),
          userRepository.findById(resultSet.getInt("created_by")),
          resultSet.getTimestamp("created_at").toLocalDateTime(),
          resultSet.getTimestamp("updated_at") != null ?
            resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
          employeeRepository.findById(resultSet.getInt("employee_id")),
          counterpartyRepository.findById(resultSet.getInt("counterparty_id"))
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve payments", ex);
      throw new DataAccessException("Database error while fetching payments", ex);
    }

    return paymentList;
  }

  public List<Payment> findByUserId(int userId) {
    String sql = "SELECT id, amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id " +
                 "FROM payments " +
                 "WHERE created_by = ?";
    List<Payment> paymentList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,userId);
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()) {
        paymentList.add(new Payment(
          resultSet.getInt("id"),
          resultSet.getBigDecimal("amount"),
          PaymentDirection.valueOf(resultSet.getString("direction")),
          PaymentCategory.valueOf(resultSet.getString("category")),
          Status.valueOf(resultSet.getString("status")),
          resultSet.getString("description"),
          userRepository.findById(resultSet.getInt("created_by")),
          resultSet.getTimestamp("created_at").toLocalDateTime(),
          resultSet.getTimestamp("updated_at") != null ?
            resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
          employeeRepository.findById(resultSet.getInt("employee_id")),
          counterpartyRepository.findById(resultSet.getInt("counterparty_id"))
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve payments", ex);
      throw new DataAccessException("Database error while fetching payments", ex);
    }

    return paymentList;
  }

  public List<Payment> findByCounterpartyId(int counterpartyId) {
    String sql = "SELECT id, amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id " +
                 "FROM payments " +
                 "WHERE counterparty_id = ?";
    List<Payment> paymentList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,counterpartyId);
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()) {
        paymentList.add(new Payment(
          resultSet.getInt("id"),
          resultSet.getBigDecimal("amount"),
          PaymentDirection.valueOf(resultSet.getString("direction")),
          PaymentCategory.valueOf(resultSet.getString("category")),
          Status.valueOf(resultSet.getString("status")),
          resultSet.getString("description"),
          userRepository.findById(resultSet.getInt("created_by")),
          resultSet.getTimestamp("created_at").toLocalDateTime(),
          resultSet.getTimestamp("updated_at") != null ?
            resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
          employeeRepository.findById(resultSet.getInt("employee_id")),
          counterpartyRepository.findById(resultSet.getInt("counterparty_id"))
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve payments", ex);
      throw new DataAccessException("Database error while fetching payments", ex);
    }

    return paymentList;
  }

  public List<Payment> findByEmployeeId(int employeeId) {
    String sql = "SELECT id, amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id " +
                 "FROM payments " +
                 "WHERE employee_id = ?";
    List<Payment> paymentList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,employeeId);
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()) {
        paymentList.add(new Payment(
          resultSet.getInt("id"),
          resultSet.getBigDecimal("amount"),
          PaymentDirection.valueOf(resultSet.getString("direction")),
          PaymentCategory.valueOf(resultSet.getString("category")),
          Status.valueOf(resultSet.getString("status")),
          resultSet.getString("description"),
          userRepository.findById(resultSet.getInt("created_by")),
          resultSet.getTimestamp("created_at").toLocalDateTime(),
          resultSet.getTimestamp("updated_at") != null ?
            resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
          employeeRepository.findById(resultSet.getInt("employee_id")),
          counterpartyRepository.findById(resultSet.getInt("counterparty_id"))
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve payments", ex);
      throw new DataAccessException("Database error while fetching payments", ex);
    }

    return paymentList;
  }

  public List<Payment> findByCategory(PaymentCategory category) {
    String sql = "SELECT id, amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id " +
                 "FROM payments " +
                 "WHERE category = ?";
    List<Payment> paymentList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setString(1,category.getDbValue());
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()) {
        paymentList.add(new Payment(
          resultSet.getInt("id"),
          resultSet.getBigDecimal("amount"),
          PaymentDirection.valueOf(resultSet.getString("direction")),
          PaymentCategory.valueOf(resultSet.getString("category")),
          Status.valueOf(resultSet.getString("status")),
          resultSet.getString("description"),
          userRepository.findById(resultSet.getInt("created_by")),
          resultSet.getTimestamp("created_at").toLocalDateTime(),
          resultSet.getTimestamp("updated_at") != null ?
            resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
          employeeRepository.findById(resultSet.getInt("employee_id")),
          counterpartyRepository.findById(resultSet.getInt("counterparty_id"))
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve payments", ex);
      throw new DataAccessException("Database error while fetching payments", ex);
    }

    return paymentList;
  }

  public List<Payment> findByDirection(PaymentDirection direction) {
    String sql = "SELECT id, amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id " +
                 "FROM payments " +
                 "WHERE direction = ?";
    List<Payment> paymentList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setString(1,direction.getDbValue());
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()) {
        paymentList.add(new Payment(
          resultSet.getInt("id"),
          resultSet.getBigDecimal("amount"),
          PaymentDirection.valueOf(resultSet.getString("direction")),
          PaymentCategory.valueOf(resultSet.getString("category")),
          Status.valueOf(resultSet.getString("status")),
          resultSet.getString("description"),
          userRepository.findById(resultSet.getInt("created_by")),
          resultSet.getTimestamp("created_at").toLocalDateTime(),
          resultSet.getTimestamp("updated_at") != null ?
            resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
          employeeRepository.findById(resultSet.getInt("employee_id")),
          counterpartyRepository.findById(resultSet.getInt("counterparty_id"))
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve payments", ex);
      throw new DataAccessException("Database error while fetching payments", ex);
    }

    return paymentList;
  }

  public List<Payment> findByDateRange(LocalDate start, LocalDate end) {
    String sql = "SELECT id, amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id " +
                 "FROM payments " +
                 "WHERE created_at BETWEEN ? AND ?";
    List<Payment> paymentList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
      preparedStatement.setTimestamp(2, Timestamp.valueOf(end.atTime(23, 59, 59)));
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()) {
        paymentList.add(new Payment(
          resultSet.getInt("id"),
          resultSet.getBigDecimal("amount"),
          PaymentDirection.valueOf(resultSet.getString("direction")),
          PaymentCategory.valueOf(resultSet.getString("category")),
          Status.valueOf(resultSet.getString("status")),
          resultSet.getString("description"),
          userRepository.findById(resultSet.getInt("created_by")),
          resultSet.getTimestamp("created_at").toLocalDateTime(),
          resultSet.getTimestamp("updated_at") != null ?
            resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
          employeeRepository.findById(resultSet.getInt("employee_id")),
          counterpartyRepository.findById(resultSet.getInt("counterparty_id"))
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve payments", ex);
      throw new DataAccessException("Database error while fetching payments", ex);
    }

    return paymentList;
  }

  public void save(Payment payment) {
    String sql = "INSERT INTO payments " +
                 "(amount, direction, category, status, description, created_by, created_at, updated_at, employee_id, counterparty_id) \n" +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setBigDecimal(1, payment.getAmount());
      preparedStatement.setString(2, payment.getPaymentDirection().getDbValue());
      preparedStatement.setString(3, payment.getCategory().getDbValue());
      preparedStatement.setString(4, payment.getStatus().getDbValue());
      preparedStatement.setString(5, payment.getDescription());
      preparedStatement.setInt(6, payment.getCreatedBy().getId());
      preparedStatement.setTimestamp(7, Timestamp.valueOf(payment.getCreatedAt()));
      preparedStatement.setTimestamp(8, payment.getUpdatedAt() != null ? Timestamp.valueOf(payment.getUpdatedAt()) : null);
      preparedStatement.setObject(9, payment.getEmployee() != null ? payment.getEmployee().getId() : null, Types.INTEGER);
      preparedStatement.setObject(10, payment.getCounterParty() != null ? payment.getCounterParty().getId() : null, Types.INTEGER);

      int affectedRows = preparedStatement.executeUpdate();

      if (affectedRows == 0) {
        throw new DataAccessException("Failed to save payment: 0 rows affected");
      }

      try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
        if (rs.next()) {
          payment.setId(rs.getInt(1));
        }
      }
    }
    catch(SQLException ex) {
      logger.error("Error saving payment: ", ex);
      throw new DataAccessException("Failed to save payment", ex);
    }
  }

  public void updateStatus(int paymentId, Status newStatus) {
    String sql = "UPDATE payments SET status = ? WHERE id = ?";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setString(1,newStatus.getDbValue());
      preparedStatement.setInt(2,paymentId);

      int affectedRows = preparedStatement.executeUpdate();

      if (affectedRows == 0) {
        throw new DataAccessException("Failed to update payment: 0 rows affected");
      }
    }
    catch(SQLException ex) {
      logger.error("Error updating payment: ", ex);
      throw new DataAccessException("Failed to update payment", ex);
    }
  }
}
