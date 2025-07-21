package com.adhitya.paymgmt.presentation;

import com.adhitya.paymgmt.dto.ReportDataDTO;
import com.adhitya.paymgmt.model.Counterparty;
import com.adhitya.paymgmt.repository.CounterpartyRepository;
import com.adhitya.paymgmt.repository.EmployeeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportPrinter {
  private static final String BORDER = "=".repeat(80);
  private static final String SECTION_BORDER = "-".repeat(80);
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yy");

  private final EmployeeRepository employeeRepository;
  private final CounterpartyRepository counterpartyRepository;

  public ReportPrinter(EmployeeRepository employeeRepository,
                       CounterpartyRepository counterpartyRepository) {
    this.employeeRepository = employeeRepository;
    this.counterpartyRepository = counterpartyRepository;
  }

  public void printReport(ReportDataDTO data) {
    printHeader(data);
    printBasicInfo(data);
    printAggregates(data);
    printCategoryBreakdown(data);
    printEntityBreakdown(data);
    printTransactionDetails(data);
  }

  private void printHeader(ReportDataDTO data) {
    System.out.println(BORDER);
    System.out.println(centerText("MONTHLY FINANCIAL REPORT"));
    System.out.println(centerText(data.startDate().getMonth() + " " + data.startDate().getYear()));
    System.out.println(BORDER);
  }

  private void printBasicInfo(ReportDataDTO data) {
    System.out.println("\n" + SECTION_BORDER);
    System.out.println("📅 PERIOD: " + formatDate(data.startDate()) + " to " + formatDate(data.endDate()));
    System.out.println("📝 TRANSACTIONS: " + data.transactionCount());
    System.out.println("🕒 REPORT GENERATED: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    System.out.println(SECTION_BORDER);
  }

  private void printAggregates(ReportDataDTO data) {
    System.out.println("\n💰 AGGREGATE TOTALS");
    System.out.println(SECTION_BORDER);
    System.out.printf("%-20s ₹%,12.2f%n", "Total Inflow:", data.totalInflow());
    System.out.printf("%-20s ₹%,12.2f%n", "Total Outflow:", data.totalOutflow());
    System.out.printf("%-20s ₹%,12.2f%n", "Net Balance:", data.netBalance());
    System.out.printf("%-20s ₹%,12.2f%n", "Avg Daily Value:", data.avgTransactionValue());
    System.out.println(SECTION_BORDER);
  }

  private void printCategoryBreakdown(ReportDataDTO data) {
    System.out.println("\n📊 CATEGORY BREAKDOWN");
    System.out.println(SECTION_BORDER);
    data.categoryTotals().forEach((category, amount) ->
      System.out.printf("%-20s ₹%,12.2f%n", category + ":", amount));
    System.out.println(SECTION_BORDER);
  }

  private void printEntityBreakdown(ReportDataDTO data) {
    System.out.println("\n👥 ENTITY BREAKDOWN");
    System.out.println(SECTION_BORDER);

    System.out.println("EMPLOYEES:");
    data.employeeTotals().forEach((id, amount) -> {
      String name = employeeRepository.findById(id).getName();
      System.out.printf("- %-20s ₹%,12.2f%n", name + ":", amount);
    });

    System.out.println("\nCOUNTERPARTIES:");
    data.counterpartyTotals().forEach((id, amount) -> {
      Counterparty cp = counterpartyRepository.findById(id);
      System.out.printf("- %-20s (%s) ₹%,12.2f%n",
        cp.getName(), cp.getPartyType(), amount);
    });
    System.out.println(SECTION_BORDER);
  }

  private void printTransactionDetails(ReportDataDTO data) {
    System.out.println("\n📜 TRANSACTION DETAILS");
    System.out.println(SECTION_BORDER);
    System.out.printf("%-10s %-12s %-10s %-15s %-20s %-20s %-10s%n",
      "Date", "Amount", "Direction", "Category", "Description", "Party", "Status");

    data.transactions().forEach(p -> {
      String partyInfo = p.getEmployee() != null
        ? employeeRepository.findById(p.getEmployee().getId()).getName()
        : counterpartyRepository.findById(p.getCounterParty().getId()).getName();

      System.out.printf("%-10s ₹%,9.2f %-10s %-15s %-20s %-20s %-10s%n",
        formatDate(p.getCreatedAt().toLocalDate()),
        p.getAmount(),
        p.getPaymentDirection(),
        p.getCategory(),
        truncate(p.getDescription(), 18),
        truncate(partyInfo, 18),
        p.getStatus());
    });
    System.out.println(SECTION_BORDER);
  }

  // Helper methods
  private String formatDate(LocalDate date) {
    return date.format(DATE_FORMAT);
  }

  private String centerText(String text) {
    return String.format("%" + (80 + text.length()) / 2 + "s", text);
  }

  private String truncate(String str, int length) {
    return str.length() > length ? str.substring(0, length-3) + "..." : str;
  }
}
