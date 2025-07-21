package com.adhitya.paymgmt.service;

import com.adhitya.paymgmt.dto.ReportDataDTO;
import com.adhitya.paymgmt.model.Payment;
import com.adhitya.paymgmt.presentation.ReportPrinter;
import com.adhitya.paymgmt.repository.CounterpartyRepository;
import com.adhitya.paymgmt.repository.EmployeeRepository;
import com.adhitya.paymgmt.util.ReportCalculator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class ReportService {
  private final PaymentService paymentService;
  private final EmployeeRepository employeeRepository;
  private final CounterpartyRepository counterpartyRepository;
  private final ReportCalculator calculator;
  private final ReportPrinter printer;

  public ReportService(
    PaymentService paymentService,
    EmployeeRepository employeeRepository,
    CounterpartyRepository counterpartyRepository
  ) {
    this.paymentService = paymentService;
    this.employeeRepository = employeeRepository;
    this.counterpartyRepository = counterpartyRepository;
    this.calculator = new ReportCalculator(paymentService, employeeRepository, counterpartyRepository);
    this.printer = new ReportPrinter(employeeRepository, counterpartyRepository);
  }

  public void generateMonthlyReport(int year, int month) {
    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.plusMonths(1).minusDays(1);

    ReportDataDTO reportData = calculator.calculateReport(start, end);

    ReportPrinter printer = new ReportPrinter(employeeRepository, counterpartyRepository);
    printer.printReport(reportData);
  }

  public void generateQuarterlyReport(int year, int quarter) {
    LocalDate start = LocalDate.of(year, (quarter-1)*3 + 1, 1);
    LocalDate end = start.plusMonths(3).minusDays(1);

    ReportDataDTO reportData = calculator.calculateReport(start, end);

    ReportPrinter printer = new ReportPrinter(employeeRepository, counterpartyRepository);
    printer.printReport(reportData);
  }

  public void exportReportAsPdf(String htmlContent, String outputPath) {
    try (FileOutputStream os = new FileOutputStream(outputPath)) {
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocumentFromString(htmlContent);
      renderer.layout();
      renderer.createPDF(os);
    } catch (Exception e) {
      throw new RuntimeException("PDF export failed: " + e.getMessage(), e);
    }
  }

  public void exportReportAsCsv(ReportDataDTO data, String outputPath) {
    CSVFormat sectionFormat = CSVFormat.DEFAULT.builder().build(); // No header for section rows
    CSVFormat tableFormat = CSVFormat.DEFAULT.builder()
      .setHeader("Date", "Amount", "Direction", "Category", "Description", "Party", "Status")
      .build();

    try (FileWriter out = new FileWriter(outputPath);
         CSVPrinter sectionPrinter = new CSVPrinter(out, sectionFormat)) {

      sectionPrinter.printRecord("Section", "Key", "Value");
      sectionPrinter.printRecord("REPORT", "Period", data.startDate() + " to " + data.endDate());
      sectionPrinter.printRecord("REPORT", "Transactions", data.transactionCount());
      sectionPrinter.printRecord("REPORT", "Report Generated", java.time.LocalDateTime.now());
      sectionPrinter.println();

      sectionPrinter.printRecord("Section", "Key", "Value");
      sectionPrinter.printRecord("AGGREGATE", "Total Inflow", data.totalInflow());
      sectionPrinter.printRecord("AGGREGATE", "Total Outflow", data.totalOutflow());
      sectionPrinter.printRecord("AGGREGATE", "Net Balance", data.netBalance());
      sectionPrinter.printRecord("AGGREGATE", "Avg Daily Value", data.avgTransactionValue());
      sectionPrinter.println();

      sectionPrinter.printRecord("Section", "Key", "Value");
      for (var entry : data.categoryTotals().entrySet()) {
        sectionPrinter.printRecord("CATEGORY", entry.getKey(), entry.getValue());
      }
      sectionPrinter.println();

      sectionPrinter.printRecord("Section", "Key", "Value");
      for (var entry : data.employeeTotals().entrySet()) {
        String name = employeeRepository.findById(entry.getKey()).getName();
        sectionPrinter.printRecord("EMPLOYEE", name, entry.getValue());
      }
      sectionPrinter.println();

      sectionPrinter.printRecord("Section", "Key", "Value");
      for (var entry : data.counterpartyTotals().entrySet()) {
        String name = counterpartyRepository.findById(entry.getKey()).getName();
        sectionPrinter.printRecord("COUNTERPARTY", name, entry.getValue());
      }

      sectionPrinter.println();

      out.write("Date,Amount,Direction,Category,Description,Party,Status\n");
      for (Payment p : data.transactions()) {
        String party = p.getEmployee() != null
          ? p.getEmployee().getName()
          : (p.getCounterParty() != null ? p.getCounterParty().getName() : "-");

        sectionPrinter.printRecord(
          p.getCreatedAt().toLocalDate(),
          p.getAmount(),
          p.getPaymentDirection(),
          p.getCategory(),
          Optional.ofNullable(p.getDescription()).orElse(""),
          party,
          p.getStatus()
        );
      }
    } catch (Exception e) {
      throw new RuntimeException("CSV export failed: " + e.getMessage(), e);
    }
  }



  public String buildHtmlReport(ReportDataDTO data, String title) {
    StringBuilder html = new StringBuilder();
    html.append("<html><head><title>").append(title).append("</title>");
    html.append("<style>")
      .append("body{font-family:Arial,sans-serif;font-size:11px;margin:20px;}")
      .append("table{border-collapse:collapse;width:100%;margin-bottom:15px;}")
      .append("th,td{border:1px solid #333;padding:4px 6px;text-align:left;white-space:normal;}")
      .append("th{background:#f6f6f6;}")
      .append(".section{margin:18px 0 8px 0;padding:0;font-size:14px;font-weight:bold;border-bottom:1px solid #aaa;}")
      .append("</style></head><body>");

    // Main header
    html.append("<h2 style='text-align:center;margin-bottom:2px;'>").append(title).append("</h2>");
    html.append("<h4 style='text-align:center;margin-top:2px;'>")
      .append(data.startDate().getMonth()).append(" ").append(data.startDate().getYear())
      .append("</h4><hr/>");

    // Period, Transactions, Report Time
    html.append("<div>")
      .append("🗓 <b>PERIOD:</b> ").append(data.startDate()).append(" to ").append(data.endDate()).append("<br />")
      .append("📝 <b>TRANSACTIONS:</b> ").append(data.transactionCount()).append("<br />")
      .append("🕒 <b>REPORT GENERATED:</b> ").append(java.time.LocalDateTime.now()).append("<br />")
      .append("</div><hr/>");

    // Aggregate totals
    html.append("<div class='section'>💰 AGGREGATE TOTALS</div>");
    html.append("<table>")
      .append("<tr><th>Total Inflow</th><th>Total Outflow</th><th>Net Balance</th><th>Avg Daily Value</th></tr>")
      .append("<tr>")
      .append("<td>₹").append(data.totalInflow()).append("</td>")
      .append("<td>₹").append(data.totalOutflow()).append("</td>")
      .append("<td>₹").append(data.netBalance()).append("</td>")
      .append("<td>₹").append(data.avgTransactionValue()).append("</td>")
      .append("</tr></table>");

    // Category Breakdown
    html.append("<div class='section'>📊 CATEGORY BREAKDOWN</div>");
    html.append("<table><tr>");
    for (var cat : data.categoryTotals().keySet()) {
      html.append("<th>").append(cat).append("</th>");
    }
    html.append("</tr><tr>");
    for (var amt : data.categoryTotals().values()) {
      html.append("<td>₹").append(amt).append("</td>");
    }
    html.append("</tr></table>");

    // Entity Breakdown
    html.append("<div class='section'>👥 ENTITY BREAKDOWN</div>");
    html.append("<table><tr><th>EMPLOYEES</th><th>AMOUNT</th></tr>");
    data.employeeTotals().forEach((id, amt) -> {
      String name = employeeRepository.findById(id).getName();
      html.append("<tr><td>").append(name).append("</td><td>₹").append(amt).append("</td></tr>");
    });
    html.append("</table>");
    html.append("<table><tr><th>COUNTERPARTIES</th><th>AMOUNT</th></tr>");
    data.counterpartyTotals().forEach((id, amt) -> {
      String name = counterpartyRepository.findById(id).getName();
      html.append("<tr><td>").append(name).append("</td><td>₹").append(amt).append("</td></tr>");
    });
    html.append("</table>");

    // Transaction Details Table
    html.append("<div class='section'>📜 TRANSACTION DETAILS</div>");
    html.append("<table>");
    html.append("<tr><th>Date</th><th>Amount</th><th>Direction</th><th>Category</th>")
      .append("<th>Description</th><th>Party</th><th>Status</th></tr>");
    for (Payment p : data.transactions()) {
      String party = p.getEmployee() != null
        ? p.getEmployee().getName()
        : (p.getCounterParty() != null ? p.getCounterParty().getName() : "-");
      html.append("<tr>");
      html.append("<td>").append(p.getCreatedAt().toLocalDate()).append("</td>")
        .append("<td>₹").append(p.getAmount()).append("</td>")
        .append("<td>").append(p.getPaymentDirection()).append("</td>")
        .append("<td>").append(p.getCategory()).append("</td>")
        .append("<td>").append(Optional.ofNullable(p.getDescription()).orElse("-")).append("</td>")
        .append("<td>").append(party).append("</td>")
        .append("<td>").append(p.getStatus()).append("</td>");
      html.append("</tr>");
    }
    html.append("</table>");

    html.append("</body></html>");
    return html.toString();
  }

  public ReportCalculator getCalculator() {
    return calculator;
  }

  public ReportDataDTO generateReportData(LocalDate start, LocalDate end) {
    return calculator.calculateReport(start, end);
  }

  public void printReport(ReportDataDTO reportData) {
    printer.printReport(reportData);
  }
}
