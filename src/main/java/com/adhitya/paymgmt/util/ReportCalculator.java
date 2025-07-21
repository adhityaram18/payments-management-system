package com.adhitya.paymgmt.util;

import com.adhitya.paymgmt.dto.ReportDataDTO;
import com.adhitya.paymgmt.model.Payment;
import com.adhitya.paymgmt.model.enums.PaymentCategory;
import com.adhitya.paymgmt.model.enums.PaymentDirection;
import com.adhitya.paymgmt.repository.CounterpartyRepository;
import com.adhitya.paymgmt.repository.EmployeeRepository;
import com.adhitya.paymgmt.service.PaymentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ReportCalculator {
  private final PaymentService paymentService;
  private final EmployeeRepository employeeRepository;
  private final CounterpartyRepository counterpartyRepository;

  public ReportCalculator(PaymentService paymentService,
                          EmployeeRepository employeeRepository,
                          CounterpartyRepository counterpartyRepository) {
    this.paymentService = paymentService;
    this.employeeRepository = employeeRepository;
    this.counterpartyRepository = counterpartyRepository;
  }

  public ReportDataDTO calculateReport(LocalDate startDate, LocalDate endDate) {
    // 1. Fetch payments for the period
    List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);

    // 2. Calculate aggregates
    BigDecimal totalInflow = calculateTotal(payments, PaymentDirection.INCOMING);
    BigDecimal totalOutflow = calculateTotal(payments, PaymentDirection.OUTGOING);
    BigDecimal netBalance = totalInflow.subtract(totalOutflow);
    BigDecimal avgTransaction = calculateAverage(payments, startDate, endDate);

    // 3. Category breakdown
    Map<PaymentCategory, BigDecimal> categoryTotals = calculateCategoryTotals(payments);

    // 4. Entity breakdown
    Map<Integer, BigDecimal> employeeTotals = calculateEmployeeTotals(payments);
    Map<Integer, BigDecimal> counterpartyTotals = calculateCounterpartyTotals(payments);

    return new ReportDataDTO(
      startDate,
      endDate,
      payments.size(),
      totalInflow,
      totalOutflow,
      netBalance,
      avgTransaction,
      categoryTotals,
      employeeTotals,
      counterpartyTotals,
      payments
    );
  }

  // Helper methods
  private BigDecimal calculateTotal(List<Payment> payments, PaymentDirection direction) {
    return payments.stream()
      .filter(p -> p.getPaymentDirection() == direction)
      .map(Payment::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal calculateAverage(List<Payment> payments, LocalDate start, LocalDate end) {
    if (payments.isEmpty()) return BigDecimal.ZERO;
    long days = ChronoUnit.DAYS.between(start, end) + 1;
    BigDecimal total = payments.stream()
      .map(Payment::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    return total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
  }

  private Map<PaymentCategory, BigDecimal> calculateCategoryTotals(List<Payment> payments) {
    return payments.stream()
      .collect(Collectors.groupingBy(
        Payment::getCategory,
        Collectors.reducing(
          BigDecimal.ZERO,
          Payment::getAmount,
          BigDecimal::add
        )
      ));
  }

  private Map<Integer, BigDecimal> calculateEmployeeTotals(List<Payment> payments) {
    return payments.stream()
      .filter(p -> p.getEmployee() != null)
      .collect(Collectors.toMap(
        p -> p.getEmployee().getId(),
        Payment::getAmount,
        BigDecimal::add
      ));
  }

  private Map<Integer, BigDecimal> calculateCounterpartyTotals(List<Payment> payments) {
    return payments.stream()
      .filter(p -> p.getCounterParty() != null)
      .collect(Collectors.toMap(
        p -> p.getCounterParty().getId(),
        Payment::getAmount,
        BigDecimal::add
      ));
  }
}
