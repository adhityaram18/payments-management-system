package com.adhitya.paymgmt.dto;

import com.adhitya.paymgmt.model.Payment;
import com.adhitya.paymgmt.model.enums.PaymentCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object encapsulating comprehensive report data
 * for a given reporting period. Includes aggregated financial
 * metrics, breakdowns by category and entities, and detailed transactions.
 *
 * @param startDate          The inclusive start date of the report period
 * @param endDate            The inclusive end date of the report period
 * @param transactionCount   Total number of transactions in the period
 * @param totalInflow        Sum total of incoming payment amounts
 * @param totalOutflow       Sum total of outgoing payment amounts
 * @param netBalance         Net balance (inflow minus outflow)
 * @param avgTransactionValue Average daily transaction amount over the period
 * @param categoryTotals     Map of payment categories to their aggregated totals
 * @param employeeTotals     Map of employee IDs to their associated payment totals
 * @param counterpartyTotals Map of counterparty IDs to their associated payment totals
 * @param transactions       Detailed list of payment transactions included in report
 */
public record ReportDataDTO(
  LocalDate startDate,
  LocalDate endDate,
  int transactionCount,
  BigDecimal totalInflow,
  BigDecimal totalOutflow,
  BigDecimal netBalance,
  BigDecimal avgTransactionValue,
  Map<PaymentCategory, BigDecimal> categoryTotals,
  Map<Integer, BigDecimal> employeeTotals,
  Map<Integer, BigDecimal> counterpartyTotals,
  List<Payment> transactions
) {}
