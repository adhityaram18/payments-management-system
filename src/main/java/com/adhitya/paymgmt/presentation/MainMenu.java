package com.adhitya.paymgmt.presentation;

import com.adhitya.paymgmt.model.User;
import com.adhitya.paymgmt.service.*;
import com.adhitya.paymgmt.repository.CounterpartyRepository;
import com.adhitya.paymgmt.repository.EmployeeRepository;

import java.util.Scanner;

public class MainMenu {
  private final Scanner scanner;
  private final AuthService authService;
  private final CounterpartyService counterpartyService;
  private final EmployeeService employeeService;
  private final PaymentService paymentService;
  private final AuditService auditService;
  private final ReportService reportService;
  private final CounterpartyRepository counterpartyRepo;
  private final EmployeeRepository employeeRepo;

  public MainMenu(Scanner scanner,
                    AuthService authService,
                    CounterpartyService counterpartyService,
                    EmployeeService employeeService,
                    PaymentService paymentService,
                    AuditService auditService,
                    ReportService reportService,
                    CounterpartyRepository counterpartyRepo,
                    EmployeeRepository employeeRepo) {
    this.scanner = scanner;
    this.authService = authService;
    this.counterpartyService = counterpartyService;
    this.employeeService = employeeService;
    this.paymentService = paymentService;
    this.auditService = auditService;
    this.reportService = reportService;
    this.counterpartyRepo = counterpartyRepo;
    this.employeeRepo = employeeRepo;
  }

  public void showGeneralMenu() {
    while (true) {
      int choice = showWelcomeMenu();
      if (choice == 2) {
        System.out.println("Thank you for using the Payment Management System.");
        break;
      }
      if (choice == 1) {
        User user = null;
        while (user == null) {
          String[] loginInput = showLoginPrompt();
          try {
            user = authService.login(loginInput[0], loginInput[1]);
            if (user == null) {
              System.out.println("❌ Invalid credentials. Try again or enter 0 to return.");
              if ("0".equals(scanner.nextLine().trim())) {
                break;
              }
            }
          } catch (Exception ex) {
            System.out.println("❌ Login failed: " + ex.getMessage());
            System.out.print("Try again? (Y/N): ");
            String again = scanner.nextLine().trim();
            if (!again.equalsIgnoreCase("Y")) break;
          }
        }
        if (user != null) {
          showMainMenu(user);  // Dispatch to role-based menu
        }
      }
    }
  }

  private int showWelcomeMenu() {
    System.out.println("====== Welcome to the Payment Management System ======");
    System.out.println("1. Login");
    System.out.println("2. Exit");
    System.out.println("======================================================");
    System.out.print("Enter your choice: ");
    return readIntChoice(1, 2);
  }

  private String[] showLoginPrompt() {
    System.out.print("Enter username: ");
    String username = scanner.nextLine().trim();
    System.out.print("Enter password: ");
    String password = scanner.nextLine().trim();
    return new String[]{username, password};
  }

  public void showMainMenu(User user) {
    // This method dispatches to the correct role UI menu
    switch (user.getRole()) {
      case ADMIN -> {
        AdminMenu adminMenu = new AdminMenu(
          scanner, authService, counterpartyService, employeeService,
          paymentService, auditService, reportService
        );
        adminMenu.showAdminMenu(user);
      }
      case FINANCE_MANAGER -> {
        FinanceManagerMenu managerMenu = new FinanceManagerMenu(
          scanner, paymentService, reportService, counterpartyRepo, employeeRepo
        );
        managerMenu.showFinanceManagerMenu(user);
      }
      case VIEWER -> {
        ViewerMenu viewerMenu = new ViewerMenu(scanner, paymentService, reportService);
        viewerMenu.showViewerMenu(user);
      }
      default -> System.out.println("Unknown role: " + user.getRole());
    }
  }

  private int readIntChoice(int min, int max) {
    while (true) {
      String line = scanner.nextLine();
      try {
        int num = Integer.parseInt(line.trim());
        if (num >= min && num <= max)
          return num;
      } catch (NumberFormatException ignored) {}
      System.out.print("Invalid choice. Please enter " + min + " or " + max + ": ");
    }
  }
}
