package com.adhitya.paymgmt.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
  public static Connection getConnection() throws SQLException {
    String url = "jdbc:postgresql://localhost:5432/miniproject1_test";
    String userName = "postgres";
    String password = "Penguin@1804";

    return DriverManager.getConnection(url,userName,password);
  }
}
