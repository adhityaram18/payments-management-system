package com.adhitya.paymgmt.dto;

import com.adhitya.paymgmt.model.Payment;
import com.adhitya.paymgmt.model.enums.PaymentCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
