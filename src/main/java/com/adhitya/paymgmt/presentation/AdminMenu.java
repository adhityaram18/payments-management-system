package com.adhitya.paymgmt.presentation;

import com.adhitya.paymgmt.model.User;
import com.adhitya.paymgmt.model.Counterparty;
import com.adhitya.paymgmt.model.Employee;
import com.adhitya.paymgmt.model.enums.Role;
import com.adhitya.paymgmt.model.enums.PartyType;
import com.adhitya.paymgmt.service.*;
import com.adhitya.paymgmt.model.Payment;
import com.adhitya.paymgmt.model.AuditLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
  private final Scanner scanner;
  // Services are provided/injected
  private final AuthService authService;
  private final CounterpartyService counterpartyService;
  private final EmployeeService employeeService;
  private final PaymentService paymentService;
  private final AuditService auditService;
  private final ReportService reportService;

  public AdminMenu(
    Scanner scanner,
    AuthService authService,
    CounterpartyService counterpartyService,
    EmployeeService employeeService,
    PaymentService paymentService,
    AuditService auditService,
    ReportService reportService
  ) {
    this.scanner = scanner;
    this.authService = authService;
    this.counterpartyService = counterpartyService;
    this.employeeService = employeeService;
    this.paymentService = paymentService;
    this.auditService = auditService;
    this.reportService = reportService;
  }

  public void showAdminMenu(User admin) {
    while (true) {
      System.out.println("========= Admin Dashboard =========");
      System.out.println("1. Register New User");
      System.out.println("2. Add Counterparty");
      System.out.println("3. Add Employee");
      System.out.println("4. View All Payments");
      System.out.println("5. Generate Report");
      System.out.println("6. View Audit Logs");
      System.out.println("7. Logout");
      System.out.println("===================================");
      System.out.print("Enter your choice (1–7): ");

      int choice = readIntChoice(1, 7);
      switch (choice) {
        case 1 -> handleUserRegistration();
        case 2 -> handleAddCounterparty();
        case 3 -> handleAddEmployee();
        case 4 -> handleViewAllPayments();
        case 5 -> handleGenerateReport();
        case 6 -> handleViewAuditLogs();
        case 7 -> {
          System.out.println("Logging out...");
          return;
        }
      }
      System.out.println(); // space before showing menu again
    }
  }

  // ------------------ MENU ACTIONS -------------------

  private void handleUserRegistration() {
    System.out.println("---- Register New User ----");
    String username = promptString("Enter username: ");
    String password = promptString("Enter password: ");
    String email = promptString("Enter email: ");

    // Prompt for role
    Role role = null;
    while (role == null) {
      System.out.print("Enter role (ADMIN/MANAGER/VIEWER): ");
      String roleIn = scanner.nextLine().trim().toUpperCase();
      try {
        role = Role.valueOf(roleIn);
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid role. Please enter ADMIN, MANAGER, or VIEWER.");
      }
    }

    try {
      authService.register(new User(0,username, password, role, email, LocalDateTime.now()));
      System.out.println("✅ User registered successfully!");
    } catch (Exception ex) {
      System.out.println("❌ Registration failed: " + ex.getMessage());
    }
  }

  private void handleAddCounterparty() {
    System.out.println("---- Add Counterparty ----");
    String name = promptString("Enter name: ");

    PartyType partyType = null;
    while (partyType == null) {
      System.out.print("Enter type (CLIENT/VENDOR): ");
      String typeInput = scanner.nextLine().trim().toUpperCase();
      try {
        partyType = PartyType.valueOf(typeInput);
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid type. Enter CLIENT or VENDOR.");
      }
    }

    String mobile = promptString("Enter mobile (optional): ");

    Counterparty cp = new Counterparty();
    cp.setName(name);
    cp.setPartyType(partyType);
    cp.setMobile(mobile.isEmpty() ? null : mobile);

    try {
      counterpartyService.addCounterparty(cp);
      System.out.println("✅ Counterparty added!");
    } catch (Exception ex) {
      System.out.println("❌ Failed to add counterparty: " + ex.getMessage());
    }
  }

  private void handleAddEmployee() {
    System.out.println("---- Add Employee ----");
    String name = promptString("Enter employee name: ");
    String dept = promptString("Enter department (optional): ");

    Employee emp = new Employee();
    emp.setName(name);
    emp.setDepartment(dept.isEmpty() ? null : dept);

    try {
      employeeService.addEmployee(emp);
      System.out.println("✅ Employee added!");
    } catch (Exception ex) {
      System.out.println("❌ Failed to add employee: " + ex.getMessage());
    }
  }

  private void handleViewAllPayments() {
    System.out.println("---- All Payments ----");
    List<Payment> payments = paymentService.getAllPayments();
    if (payments.isEmpty()) {
      System.out.println("(No payments found.)");
      return;
    }
    // Table-like format
    System.out.printf("%-5s %-12s %-10s %-12s %-12s %-10s %-15s%n",
      "ID", "Amount", "Direction", "Category","Status", "CreatedBy", "CreatedAt");
    System.out.println("-".repeat(80));
    for (Payment p : payments) {
      System.out.printf("%-5d ₹%10.2f %-10s %-12s %-12s %-10s %-15s%n",
        p.getId(),
        p.getAmount(),
        p.getPaymentDirection(),
        p.getCategory(),
        p.getStatus(),
        p.getCreatedBy() != null ? p.getCreatedBy().getUsername() : "-",
        p.getCreatedAt());
    }
  }

  private void handleGenerateReport() {
    System.out.println("---- Generate Report ----");
    System.out.println("1. Monthly Report");
    System.out.println("2. Quarterly Report");
    int reportChoice = readIntChoice(1, 2);

    if (reportChoice == 1) {
      int year = promptInt("Enter year (e.g. 2024): ");
      int month = promptInt("Enter month (1-12): ");
      reportService.generateMonthlyReport(year, month);
    } else {
      int year = promptInt("Enter year (e.g. 2024): ");
      int quarter = promptInt("Enter quarter (1-4): ");
      reportService.generateQuarterlyReport(year, quarter);
    }
  }

  private void handleViewAuditLogs() {
    System.out.println("---- Audit Logs ----");
    List<AuditLog> logs = auditService.getAllAuditLogs();
    if (logs.isEmpty()) {
      System.out.println("(No audit logs found.)");
      return;
    }
    System.out.printf("%-5s %-12s %-20s %-20s %-10s %-19s%n",
      "ID", "PaymentID", "Field", "By", "Old->New", "When");
    System.out.println("-".repeat(90));
    for (AuditLog log : logs) {
      String by = log.getChangedBy() != null ? log.getChangedBy().getUsername() : "-";
      String oldnew = (log.getOldValue() + " → " + log.getNewValue());
      System.out.printf("%-5d %-12s %-20s %-20s %-10s %-19s%n",
        log.getId(),
        log.getPayment() != null ? log.getPayment().getId() : "-",
        log.getFieldChanged(),
        by,
        oldnew,
        log.getChangedAt());
    }
  }

  // ------------------ UTILS -------------------

  private int readIntChoice(int min, int max) {
    while (true) {
      String line = scanner.nextLine();
      try {
        int num = Integer.parseInt(line.trim());
        if (num >= min && num <= max)
          return num;
      } catch (NumberFormatException ignored) {}
      System.out.print("Invalid choice. Please enter " + min + "–" + max + ": ");
    }
  }
  private int promptInt(String label) {
    while (true) {
      System.out.print(label);
      try {
        return Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException ignored) {
        System.out.println("Invalid number.");
      }
    }
  }
  private String promptString(String label) {
    System.out.print(label);
    return scanner.nextLine().trim();
  }
}

