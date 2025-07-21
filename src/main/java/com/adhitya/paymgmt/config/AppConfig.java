package com.adhitya.paymgmt.config;

import com.adhitya.paymgmt.presentation.MainMenu;
import com.adhitya.paymgmt.repository.AuditLogRepository;
import com.adhitya.paymgmt.repository.CounterpartyRepository;
import com.adhitya.paymgmt.repository.EmployeeRepository;
import com.adhitya.paymgmt.repository.PaymentRepository;
import com.adhitya.paymgmt.repository.UserRepository;
import com.adhitya.paymgmt.service.AuditService;
import com.adhitya.paymgmt.service.AuthService;
import com.adhitya.paymgmt.service.CounterpartyService;
import com.adhitya.paymgmt.service.EmployeeService;
import com.adhitya.paymgmt.service.PaymentService;
import com.adhitya.paymgmt.service.ReportService;

import java.util.Scanner;

public class AppConfig {
  public final AuthService authService;
  public final PaymentService paymentService;
  public final ReportService reportService;
  public final AuditService auditService;
  public final AuthService userService;
  public final EmployeeService employeeService;
  public final CounterpartyService counterpartyService;

  public final Scanner scanner;
  public final MainMenu mainMenu;

  public AppConfig() {
    UserRepository userRepo = new UserRepository();
    EmployeeRepository employeeRepo = new EmployeeRepository();
    CounterpartyRepository counterpartyRepo = new CounterpartyRepository();
    PaymentRepository paymentRepo = new PaymentRepository(userRepo,employeeRepo,counterpartyRepo);
    AuditLogRepository auditRepo = new AuditLogRepository(paymentRepo,userRepo);

    this.userService = new AuthService(userRepo);
    this.authService = new AuthService(userRepo);
    this.paymentService = new PaymentService(paymentRepo, auditRepo, userRepo);
    this.auditService = new AuditService(auditRepo);
    this.reportService = new ReportService(paymentService, employeeRepo, counterpartyRepo);
    this.employeeService = new EmployeeService(employeeRepo);
    this.counterpartyService = new CounterpartyService(counterpartyRepo);
    this.scanner = new Scanner(System.in);
    this.mainMenu = new MainMenu(scanner,authService,counterpartyService,employeeService,paymentService,auditService,reportService,counterpartyRepo,employeeRepo);
  }

  public MainMenu getMainMenu() {
    return this.mainMenu;
  }
}

