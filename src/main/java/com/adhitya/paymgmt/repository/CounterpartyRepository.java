package com.adhitya.paymgmt.repository;

import com.adhitya.paymgmt.config.DatabaseConfig;
import com.adhitya.paymgmt.exception.DataAccessException;
import com.adhitya.paymgmt.model.Counterparty;
import com.adhitya.paymgmt.model.enums.PartyType;
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

public class CounterpartyRepository {
  private static final Logger logger = LoggerFactory.getLogger(CounterpartyRepository.class);

  public Counterparty findById(int id) {
    String sql = "SELECT id, name, type, mobile, created_at FROM counterparties WHERE id = ?";

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setInt(1,id);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          Timestamp createdAtTs = resultSet.getTimestamp("created_at");
          LocalDateTime createdAt = (createdAtTs != null) ? createdAtTs.toLocalDateTime() : null;

          return new Counterparty(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            PartyType.valueOf(resultSet.getString("type")),
            resultSet.getString("mobile"),
            createdAt
          );
        }
      }
    }
    catch (SQLException ex) {
      logger.error("Failed to retrieve counterparty", ex);
      throw new DataAccessException("Database error while fetching counterparty", ex);
    }

    return null;
  }

  public List<Counterparty> findAllByType(PartyType type) {
    String sql = "SELECT id, name, type, mobile, created_at FROM counterparties WHERE type = ?";
    List<Counterparty> counterpartyList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setString(1,type.getDbValue());
      ResultSet resultSet = preparedStatement.executeQuery();

      while(resultSet.next()) {
        Timestamp createdAtTs = resultSet.getTimestamp("created_at");
        LocalDateTime createdAt = (createdAtTs != null) ? createdAtTs.toLocalDateTime() : null;

        counterpartyList.add(
          new Counterparty(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            PartyType.valueOf(resultSet.getString("type")),
            resultSet.getString("mobile"),
            createdAt
          )
        );
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve counterparties", ex);
      throw new DataAccessException("Database error while fetching counterparties", ex);
    }

    return counterpartyList;
  }

  public List<Counterparty> findAll() {
    String sql = "SELECT id, name, type, mobile, created_at FROM counterparties";
    List<Counterparty> counterpartyList = new ArrayList<>();

    try (Connection connection = DatabaseConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery();
    )
    {
      while(resultSet.next()) {
        Timestamp createdAtTs = resultSet.getTimestamp("created_at");
        LocalDateTime createdAt = (createdAtTs != null) ? createdAtTs.toLocalDateTime() : null;

        counterpartyList.add(
          new Counterparty(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            PartyType.valueOf(resultSet.getString("type")),
            resultSet.getString("mobile"),
            createdAt
          )
        );
      }
    }
    catch(SQLException ex) {
      logger.error("Failed to retrieve counterparties", ex);
      throw new DataAccessException("Database error while fetching counterparties", ex);
    }

    return counterpartyList;
  }

  public void save(Counterparty counterparty) {

    if (counterparty.getName() == null || counterparty.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null/empty");
    }

    if (counterparty.getMobile() != null && !counterparty.getMobile().matches("\\+?[0-9]+")) {
      throw new IllegalArgumentException("Invalid mobile number format");
    }

    String sql = "INSERT INTO counterparties (name, type, mobile, created_at) VALUES (?, ?, ?, ?)";

    try(Connection connection = DatabaseConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    ) {
      preparedStatement.setString(1, counterparty.getName());
      preparedStatement.setString(2,counterparty.getPartyType().getDbValue());
      preparedStatement.setString(3,counterparty.getMobile());

      if (counterparty.getCreatedAt() != null) {
        preparedStatement.setTimestamp(4, Timestamp.valueOf(counterparty.getCreatedAt()));
      }
      else {
        preparedStatement.setTimestamp(4, null);
      }

      int affectedRows = preparedStatement.executeUpdate();

      if (affectedRows == 0) {
        throw new DataAccessException("Failed to save counterparty: 0 rows affected");
      }

      try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
        if (rs.next()) {
          counterparty.setId(rs.getInt(1));
        }
      }
    }
    catch (SQLException ex) {
      logger.error("Error saving counterparty: " + counterparty.getName(), ex);
      throw new DataAccessException("Failed to save counterparty", ex);
    }
  }
}
