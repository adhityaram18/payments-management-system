package com.adhitya.paymgmt.presentation;

import com.adhitya.paymgmt.dto.ReportDataDTO;
import com.adhitya.paymgmt.exception.EmptyResultException;
import com.adhitya.paymgmt.model.*;
import com.adhitya.paymgmt.model.enums.*;
import com.adhitya.paymgmt.service.*;
import com.adhitya.paymgmt.repository.CounterpartyRepository;
import com.adhitya.paymgmt.repository.EmployeeRepository;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class FinanceManagerMenu {
  private final Scanner scanner;
  private final PaymentService paymentService;
  private final ReportService reportService;
  private final CounterpartyRepository counterpartyRepo;
  private final EmployeeRepository employeeRepo;

  public FinanceManagerMenu(
    Scanner scanner,
    PaymentService paymentService,
    ReportService reportService,
    CounterpartyRepository counterpartyRepo,
    EmployeeRepository employeeRepo
  ) {
    this.scanner = scanner;
    this.paymentService = paymentService;
    this.reportService = reportService;
    this.counterpartyRepo = counterpartyRepo;
    this.employeeRepo = employeeRepo;
  }

  public void showFinanceManagerMenu(User manager) {
    while (true) {
      System.out.println("======= Finance Manager Dashboard =======");
      System.out.println("1. Add Payment");
      System.out.println("2. Update Payment Status");
      System.out.println("3. View Payments");
      System.out.println("4. Generate Report");
      System.out.println("5. Logout");
      System.out.println("=========================================");
      System.out.print("Enter your choice (1–5): ");

      int choice = readIntChoice(1, 5);
      switch (choice) {
        case 1 -> handleAddPayment(manager);
        case 2 -> handleUpdatePaymentStatus(manager);
        case 3 -> handleViewPayments(manager);
        case 4 -> handleGenerateReport();
        case 5 -> {
          System.out.println("Logging out...");
          return;
        }
      }
      System.out.println();
    }
  }

  // ========== HANDLERS ==========

  private void handleAddPayment(User manager) {
    System.out.println("---- Add Payment ----");

    BigDecimal amount = promptBigDecimal("Amount: ₹");
    PaymentDirection direction = promptEnumSelection("Direction", PaymentDirection.class);
    PaymentCategory category = promptEnumSelection("Category", PaymentCategory.class);

    String description = promptString("Description (optional): ");
    Employee employee = null;
    Counterparty counterparty = null;

    if (category == PaymentCategory.SALARY) {
      // Employee selection
      List<Employee> emps = employeeRepo.findAll();
      if (emps.isEmpty()) {
        System.out.println("No employees found.");
        return;
      }
      emps.forEach(e -> System.out.printf("[%d] %s\n", e.getId(), e.getName()));
      int empId = promptInt("Select Employee ID: ");
      employee = employeeRepo.findById(empId);
      if (employee == null) {
        System.out.println("Invalid Employee ID.");
        return;
      }
    } else {
      // Counterparty selection
      List<Counterparty> cps = counterpartyRepo.findAll();
      if (cps.isEmpty()) {
        System.out.println("No counterparties found.");
        return;
      }
      cps.forEach(c -> System.out.printf("[%d] %s (%s)\n", c.getId(), c.getName(), c.getPartyType()));
      int cpId = promptInt("Select Counterparty ID: ");
      counterparty = counterpartyRepo.findById(cpId);
      if (counterparty == null) {
        System.out.println("Invalid Counterparty ID.");
        return;
      }
    }

    Payment payment = new Payment();
    payment.setAmount(amount);
    payment.setPaymentDirection(direction);
    payment.setCategory(category);
    payment.setDescription(description.isEmpty() ? null : description);
    payment.setCreatedBy(manager);
    payment.setEmployee(employee);
    payment.setCounterParty(counterparty);
    payment.setStatus(Status.PENDING);
    payment.setCreatedAt(LocalDateTime.now());

    try {
      paymentService.addPayment(payment);
      System.out.println("✅ Payment added successfully!");
    } catch (Exception ex) {
      System.out.println("❌ Failed to add payment: " + ex.getMessage());
    }
  }

  private void handleUpdatePaymentStatus(User manager) {
    System.out.println("---- Update Payment Status ----");

    // Optionally: filter by date, direction, etc.
    List<Payment> list = paymentService.getAllPayments();
    if (list.isEmpty()) {
      System.out.println("No payments found.");
      return;
    }

    System.out.println("Payment List:");
    System.out.printf("%-5s %-10s %-10s %-12s %-10s%n", "ID", "Amount", "Category", "Status", "Date");
    for (Payment p : list) {
      System.out.printf("%-5d ₹%8.2f %-10s %-12s %-10s%n",
        p.getId(), p.getAmount(), p.getCategory(), p.getStatus(), p.getCreatedAt().toLocalDate());
    }

    int paymentId = promptInt("Enter Payment ID to update: ");
    Payment payment = paymentService.findById(paymentId);
    if (payment == null) {
      System.out.println("Not found.");
      return;
    }
    Status newStatus = promptEnumSelection("New Status", Status.class);

    try {
      paymentService.updatePaymentStatus(paymentId, newStatus, manager.getId()); // Audit log automatically
      System.out.println("✅ Status updated.");
    } catch (Exception ex) {
      System.out.println("❌ Update failed: " + ex.getMessage());
    }
  }

  private void handleViewPayments(User manager) {
    System.out.println("---- View Payments ----");
    System.out.println("Filter by:");
    System.out.println("1. All");
    System.out.println("2. Date Range");
    System.out.println("3. Direction");
    System.out.println("4. Category");
    System.out.print("Enter choice (1-4): ");
    int choice = readIntChoice(1, 4);
    List<Payment> payments;

    switch (choice) {
      case 1 -> payments = paymentService.getAllPayments();
      case 2 -> {
        LocalDate start = promptDate("From date (YYYY-MM-DD): ");
        LocalDate end = promptDate("To date (YYYY-MM-DD): ");
        payments = paymentService.getPaymentsByDateRange(start, end);
      }
      case 3 -> {
        PaymentDirection dir = promptEnumSelection("Direction", PaymentDirection.class);
        payments = paymentService.getPaymentsByPaymentDirection(dir);
      }
      case 4 -> {
        PaymentCategory cat = promptEnumSelection("Category", PaymentCategory.class);
        payments = paymentService.getPaymentsByCategory(cat);
      }
      default -> payments = paymentService.getAllPayments();
    }

    if (payments == null || payments.isEmpty()) {
      System.out.println("No payments to display.");
    } else {
      System.out.printf("%-5s %-10s %-10s %-10s %-10s %-10s %-20s%n",
        "ID", "Amount", "Direction", "Category", "Status", "By", "Date");
      for (Payment p : payments) {
        System.out.printf("%-5d ₹%8.2f %-10s %-10s %-10s %-10s %-20s%n",
          p.getId(), p.getAmount(), p.getPaymentDirection(), p.getCategory(),
          p.getStatus(), p.getCreatedBy().getUsername(), p.getCreatedAt());
      }
    }
  }

  private void handleGenerateReport() {
    System.out.println("---- Generate Report ----");
    System.out.println("1. Monthly Report");
    System.out.println("2. Quarterly Report");
    System.out.print("Enter your choice (1-2): ");
    int reportType = readIntChoice(1, 2);

    int year = promptInt("Enter year (e.g. 2024): ");
    LocalDate start, end;
    String title;

    if (reportType == 1) {
      int month = promptInt("Enter month (1-12): ");
      start = LocalDate.of(year, month, 1);
      end = start.plusMonths(1).minusDays(1);
      title = "Monthly Report for " + year + "-" + String.format("%02d", month);
    } else {
      int quarter = promptInt("Enter quarter (1-4): ");
      int startMonth = (quarter - 1) * 3 + 1;
      start = LocalDate.of(year, startMonth, 1);
      end = start.plusMonths(3).minusDays(1);
      title = "Quarterly Report for Q" + quarter + " " + year;
    }

    ReportDataDTO reportData;

    try {
      reportData = reportService.getCalculator().calculateReport(start, end);

      if (reportData == null || reportData.transactions().isEmpty()) {
        System.out.println("No payments found for the selected period.");
        return;
      }

      reportService.printReport(reportData);
    } catch (EmptyResultException e) {
      System.out.println("No payments found for the selected period.");
      return;
    } catch (DateTimeException e) {
      System.out.println("Invalid Date: " + e);
      return;
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      return;
    }

    System.out.println("Export report?");
    System.out.println("  1. PDF");
    System.out.println("  2. CSV");
    System.out.println("  3. Both PDF and CSV");
    System.out.println("  4. No export");
    System.out.println("  5. HTML view in browser");
    System.out.print("Enter your choice (1-5): ");
    int exportChoice = readIntChoice(1, 5);

    String html = null; // Generate only once if needed

    if (exportChoice == 1 || exportChoice == 3) {
      if (html == null) html = reportService.buildHtmlReport(reportData, title);
      String pdfPath = promptString("Enter PDF output file path (e.g., report.pdf): ");
      reportService.exportReportAsPdf(html, pdfPath);
      System.out.println("PDF exported to: " + pdfPath);
    }
    if (exportChoice == 2 || exportChoice == 3) {
      String csvPath = promptString("Enter CSV output file path (e.g., report.csv): ");
      reportService.exportReportAsCsv(reportData, csvPath);
      System.out.println("CSV exported to: " + csvPath);
    }
    if (exportChoice == 5) {
      if (html == null) html = reportService.buildHtmlReport(reportData, title);
      String htmlPath = promptString("Enter HTML output file path (e.g., report.html): ");
      try (FileWriter writer = new FileWriter(htmlPath)) {
        writer.write(html);
        System.out.println("HTML exported to: " + htmlPath);
        try {
          java.awt.Desktop.getDesktop().browse(new java.io.File(htmlPath).toURI());
          System.out.println("Opened in default browser.");
        } catch (Exception e) {
          System.out.println("Could not open in browser automatically. Please open the file manually.");
        }
      } catch (Exception e) {
        System.out.println("Failed to save HTML: " + e.getMessage());
      }
    }
  }


  // ========== UTILITIES ==========

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

  private BigDecimal promptBigDecimal(String label) {
    while (true) {
      System.out.print(label);
      String input = scanner.nextLine().trim().replace(",", "");
      try {
        return new BigDecimal(input);
      } catch (Exception e) {
        System.out.println("Invalid amount.");
      }
    }
  }

  private int promptInt(String label) {
    while (true) {
      System.out.print(label);
      String input = scanner.nextLine().trim();
      try {
        return Integer.parseInt(input);
      } catch (Exception e) {
        System.out.println("Invalid number.");
      }
    }
  }

  private String promptString(String label) {
    System.out.print(label);
    return scanner.nextLine().trim();
  }

  private LocalDate promptDate(String label) {
    while (true) {
      System.out.print(label);
      String input = scanner.nextLine().trim();
      try {
        return LocalDate.parse(input);
      } catch (Exception e) {
        System.out.println("Invalid date (use YYYY-MM-DD).");
      }
    }
  }

  private <T extends Enum<T>> T promptEnumSelection(String label, Class<T> enumClass) {
    while (true) {
      System.out.print(label + " ");
      String options = String.join("/",
        java.util.Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray(String[]::new));
      System.out.print("(" + options + "): ");
      String value = scanner.nextLine().trim().toUpperCase();
      try {
        return Enum.valueOf(enumClass, value);
      } catch (Exception e) {
        System.out.println("Invalid input. Use one of: " + options);
      }
    }
  }
}
