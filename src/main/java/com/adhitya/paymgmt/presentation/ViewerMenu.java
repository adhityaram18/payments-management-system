package com.adhitya.paymgmt.presentation;

import com.adhitya.paymgmt.model.Payment;
import com.adhitya.paymgmt.model.User;
import com.adhitya.paymgmt.model.enums.PaymentCategory;
import com.adhitya.paymgmt.model.enums.PaymentDirection;
import com.adhitya.paymgmt.service.PaymentService;
import com.adhitya.paymgmt.service.ReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ViewerMenu {
  private final Scanner scanner;
  private final PaymentService paymentService;
  private final ReportService reportService;

  public ViewerMenu(Scanner scanner, PaymentService paymentService, ReportService reportService) {
    this.scanner = scanner;
    this.paymentService = paymentService;
    this.reportService = reportService;
  }

  public void showViewerMenu(User viewer) {
    while (true) {
      System.out.println("========== Viewer Dashboard ==========");
      System.out.println("1. View Payments");
      System.out.println("2. Generate Report");
      System.out.println("3. Logout");
      System.out.println("======================================");
      System.out.print("Enter your choice (1–3): ");
      int choice = readIntChoice(1, 3);
      switch (choice) {
        case 1 -> handleViewPayments();
        case 2 -> handleGenerateReport();
        case 3 -> {
          System.out.println("Logging out...");
          return;
        }
      }
      System.out.println();
    }
  }

  // ================= MENU ACTIONS =================

  private void handleViewPayments() {
    System.out.println("---- View Payments ----");
    System.out.println("Filter by:");
    System.out.println("1. All");
    System.out.println("2. Date Range");
    System.out.println("3. Category");
    System.out.print("Choose filter [1-3]: ");
    int filter = readIntChoice(1, 3);

    List<Payment> payments;
    switch (filter) {
      case 1 -> payments = paymentService.getAllPayments();
      case 2 -> {
        LocalDate start = promptDate("From date (YYYY-MM-DD): ");
        LocalDate end = promptDate("To date (YYYY-MM-DD): ");
        payments = paymentService.getPaymentsByDateRange(start, end);
      }
      case 3 -> {
        PaymentCategory cat = promptEnumSelection("Category", PaymentCategory.class);
        payments = paymentService.getPaymentsByCategory(cat);
      }
      default -> payments = paymentService.getAllPayments();
    }

    if (payments == null || payments.isEmpty()) {
      System.out.println("No payments to display.");
    } else {
      System.out.printf("%-10s %-10s %-10s %-12s %-16s %-12s %-10s%n",
        "Date", "Amount", "Direction", "Category", "Description", "Status", "By");
      for (Payment p : payments) {
        System.out.printf("%-10s ₹%8.2f %-10s %-12s %-16s %-12s %-10s%n",
          p.getCreatedAt().toLocalDate(),
          p.getAmount(),
          p.getPaymentDirection(),
          p.getCategory(),
          truncate(p.getDescription(), 15),
          p.getStatus(),
          p.getCreatedBy() != null ? p.getCreatedBy().getUsername() : "-"
        );
      }
    }
  }

  private void handleGenerateReport() {
    System.out.println("---- Generate Report ----");
    System.out.print("1. Monthly  2. Quarterly  > ");
    int type = readIntChoice(1,2);

    int year = promptInt("Enter year (e.g. 2025): ");
    if (type == 1) {
      int month = promptInt("Enter month (1-12): ");
      reportService.generateMonthlyReport(year, month);
    } else {
      int q = promptInt("Enter quarter (1-4): ");
      reportService.generateQuarterlyReport(year, q);
    }
  }

  // ================= UTILITIES =================

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

  private String promptString(String label) {
    System.out.print(label);
    return scanner.nextLine().trim();
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
        System.out.println("Invalid input. Options: " + options);
      }
    }
  }

  private String truncate(String str, int length) {
    if (str == null) return "-";
    return str.length() > length ? str.substring(0, length - 3) + "..." : str;
  }
}
