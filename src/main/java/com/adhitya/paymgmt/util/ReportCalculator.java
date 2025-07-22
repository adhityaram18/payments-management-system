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

/**
 * Utility class responsible for calculating detailed financial reports
 * over a specified date range.
 * It aggregates payment data by various dimensions including direction,
 * category, and entities (employees, counterparties).
 */
public class ReportCalculator {
  private final PaymentService paymentService;
  private final EmployeeRepository employeeRepository;
  private final CounterpartyRepository counterpartyRepository;

  /**
   * Constructs the ReportCalculator with dependencies on PaymentService and Repositories.
   *
   * @param paymentService        Service to access payment records
   * @param employeeRepository    Repository for employee data lookup
   * @param counterpartyRepository Repository for counterparty data lookup
   */
  public ReportCalculator(PaymentService paymentService,
                          EmployeeRepository employeeRepository,
                          CounterpartyRepository counterpartyRepository) {
    this.paymentService = paymentService;
    this.employeeRepository = employeeRepository;
    this.counterpartyRepository = counterpartyRepository;
  }

  /**
   * Calculates a comprehensive monthly or quarterly report for the given date range.
   * Performs data fetch, aggregation, and breakdown computations.
   *
   * @param startDate inclusive start date of report period
   * @param endDate   inclusive end date of report period
   * @return assembled ReportDataDTO containing all computed summary data
   */
  public ReportDataDTO calculateReport(LocalDate startDate, LocalDate endDate) {
    // 1. Fetch all payments within the specified date range.
    List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);

    // 2. Calculate total inflow and outflow based on payment direction.
    BigDecimal totalInflow = calculateTotal(payments, PaymentDirection.INCOMING);
    BigDecimal totalOutflow = calculateTotal(payments, PaymentDirection.OUTGOING);

    // Net balance is inflow minus outflow.
    BigDecimal netBalance = totalInflow.subtract(totalOutflow);

    // Average daily transaction value over the period.
    BigDecimal avgTransaction = calculateAverage(payments, startDate, endDate);

    // 3. Compute totals by payment category for breakdown analysis.
    Map<PaymentCategory, BigDecimal> categoryTotals = calculateCategoryTotals(payments);

    // 4. Compute totals by employee (internal payments).
    Map<Integer, BigDecimal> employeeTotals = calculateEmployeeTotals(payments);

    // 5. Compute totals by counterparty (external payments).
    Map<Integer, BigDecimal> counterpartyTotals = calculateCounterpartyTotals(payments);

    // Assemble and return the full report data transfer object.
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

  /**
   * Helper method to calculate total payments for a given direction (INCOMING or OUTGOING).
   *
   * @param payments   list of Payment entities
   * @param direction  payment direction to filter by
   * @return sum of amounts matching the direction
   */
  private BigDecimal calculateTotal(List<Payment> payments, PaymentDirection direction) {
    return payments.stream()
      .filter(p -> p.getPaymentDirection() == direction)
      .map(Payment::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Helper method to calculate average daily transaction amount over the report period.
   * Returns zero if no transactions exist.
   *
   * @param payments list of Payment entities
   * @param start    inclusive start date of period
   * @param end      inclusive end date of period
   * @return average daily amount rounded to two decimal places
   */
  private BigDecimal calculateAverage(List<Payment> payments, LocalDate start, LocalDate end) {
    if (payments.isEmpty()) return BigDecimal.ZERO;
    long days = ChronoUnit.DAYS.between(start, end) + 1;
    BigDecimal total = payments.stream()
      .map(Payment::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    return total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
  }

  /**
   * Groups payments by category and sums their amounts.
   *
   * @param payments list of Payment entities
   * @return map of PaymentCategory to total amount in that category
   */
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

  /**
   * Groups internal payments by employee and sums their amounts.
   * Filters out payments not associated with any employee.
   *
   * @param payments list of Payment entities
   * @return map of employee ID to total payment amount
   */
  private Map<Integer, BigDecimal> calculateEmployeeTotals(List<Payment> payments) {
    return payments.stream()
      .filter(p -> p.getEmployee() != null)
      .collect(Collectors.toMap(
        p -> p.getEmployee().getId(),
        Payment::getAmount,
        BigDecimal::add
      ));
  }

  /**
   * Groups external payments by counterparty and sums their amounts.
   * Filters out payments not associated with any counterparty.
   *
   * @param payments list of Payment entities
   * @return map of counterparty ID to total payment amount
   */
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
