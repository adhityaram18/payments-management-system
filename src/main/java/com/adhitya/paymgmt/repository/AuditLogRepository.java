package com.adhitya.paymgmt.repository;

import com.adhitya.paymgmt.config.DatabaseConfig;
import com.adhitya.paymgmt.exception.DataAccessException;
import com.adhitya.paymgmt.model.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AuditLogRepository {
  private static final Logger logger = LoggerFactory.getLogger(AuditLogRepository.class);
  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;

  public AuditLogRepository(PaymentRepository paymentRepository, UserRepository userRepository) {
    this.paymentRepository = paymentRepository;
    this.userRepository = userRepository;
  }

  public List<AuditLog> findByPaymentId(int paymentId) {
    String sql = "SELECT id, payment_id, field_changed, old_value, new_value, changed_by, changed_at" +
                 "FROM audit_logs " +
                 "WHERE payment_id = ?";
    List<AuditLog> auditLogList = new ArrayList<>();

    try(Connection connection = DatabaseConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,paymentId);
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()) {
        auditLogList.add(new AuditLog(
          resultSet.getInt("id"),
          paymentRepository.findById(resultSet.getInt("payment_id")),
          resultSet.getString("field_changed"),
          resultSet.getString("old_value"),
          resultSet.getString("new_value"),
          userRepository.findById(resultSet.getInt("changed_by")),
          resultSet.getTimestamp("changed_at").toLocalDateTime()
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Error finding audit logs by paymentId: " + paymentId, ex);
      throw new DataAccessException("Failed to find audit log/logs", ex);
    }

    return auditLogList;
  }

  public List<AuditLog> findAll() {
    String sql = "SELECT id, payment_id, field_changed, old_value, new_value, changed_by, changed_at " +
                 "FROM audit_logs";
    List<AuditLog> auditLogList = new ArrayList<>();

    try(Connection connection = DatabaseConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
    ) {
      while(resultSet.next()) {
        auditLogList.add(new AuditLog(
          resultSet.getInt("id"),
          paymentRepository.findById(resultSet.getInt("payment_id")),
          resultSet.getString("field_changed"),
          resultSet.getString("old_value"),
          resultSet.getString("new_value"),
          userRepository.findById(resultSet.getInt("changed_by")),
          resultSet.getTimestamp("changed_at").toLocalDateTime()
        ));
      }
    }
    catch(SQLException ex) {
      logger.error("Error finding audit logs", ex);
      throw new DataAccessException("Failed to find audit log/logs", ex);
    }

    return auditLogList;
  }

  public void save(AuditLog auditLog) {
    if (auditLog.getPayment() == null || auditLog.getChangedBy() == null) {
      throw new IllegalArgumentException("Payment/changedBy cannot be null");
    }

    String sql = "INSERT INTO audit_logs " +
                 "(payment_id, field_changed, old_value, new_value, changed_by, changed_at) \n" +
                 "VALUES (?, ?, ?, ?, ?, ?)";

    try(Connection connection = DatabaseConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,auditLog.getPayment().getId());
      preparedStatement.setString(2, auditLog.getFieldChanged());
      preparedStatement.setString(3,auditLog.getOldValue());
      preparedStatement.setString(4,auditLog.getNewValue());
      preparedStatement.setInt(5,auditLog.getChangedBy().getId());
      preparedStatement.setTimestamp(6, Timestamp.valueOf(auditLog.getChangedAt()));

      int affectedRows = preparedStatement.executeUpdate();

      if (affectedRows == 0) {
        throw new DataAccessException("Failed to save audit log 0 rows affected");
      }

      try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
        if (rs.next()) {
          auditLog.setId(rs.getInt(1));
        }
      }
    }
    catch(SQLException ex) {
      logger.error("Error saving audit log for payment ID: " + auditLog.getPayment().getId(), ex);
      throw new DataAccessException("Failed to save audit log", ex);
    }
  }
}
