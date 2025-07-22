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

/**
 * Service class responsible for handling report generation,
 * export (PDF, CSV, HTML), and printing tasks related to payment summaries.
 */
public class ReportService {
  private final PaymentService paymentService;
  private final EmployeeRepository employeeRepository;
  private final CounterpartyRepository counterpartyRepository;
  private final ReportCalculator calculator;
  private final ReportPrinter printer;

  /**
   * Constructs ReportService with required services and repositories.
   * Initializes calculator and printer utilities.
   */
  public ReportService(
    PaymentService paymentService,
    EmployeeRepository employeeRepository,
    CounterpartyRepository counterpartyRepository
  ) {
    this.paymentService = paymentService;
    this.employeeRepository = employeeRepository;
    this.counterpartyRepository = counterpartyRepository;
    // Calculator performs all report data computation and aggregation
    this.calculator = new ReportCalculator(paymentService, employeeRepository, counterpartyRepository);
    // Printer handles formatted console output of report data
    this.printer = new ReportPrinter(employeeRepository, counterpartyRepository);
  }

  /**
   * Generates and prints a monthly report for the specified year and month.
   * @param year  the year for the report period
   * @param month the month (1-12) for the report period
   */
  public void generateMonthlyReport(int year, int month) {
    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.plusMonths(1).minusDays(1);

    ReportDataDTO reportData = calculator.calculateReport(start, end);

    // Use a new printer instance to output the report data to console
    ReportPrinter printer = new ReportPrinter(employeeRepository, counterpartyRepository);
    printer.printReport(reportData);
  }

  /**
   * Generates and prints a quarterly report for the specified year and quarter.
   * @param year    the year for the report
   * @param quarter the quarter (1-4)
   */
  public void generateQuarterlyReport(int year, int quarter) {
    LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
    LocalDate end = start.plusMonths(3).minusDays(1);

    ReportDataDTO reportData = calculator.calculateReport(start, end);

    // Console print of report data
    ReportPrinter printer = new ReportPrinter(employeeRepository, counterpartyRepository);
    printer.printReport(reportData);
  }

  /**
   * Exports the given HTML content as a PDF file at the specified output path.
   * Uses Flying Saucer (ITextRenderer) for rendering.
   * @param htmlContent full HTML string to convert to PDF
   * @param outputPath  filesystem path to save PDF file
   */
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

  /**
   * Exports the entire report data including metadata, aggregates,
   * breakdowns, and transactions as a structured CSV file.
   * The CSV is divided into sections repeating headers for clarity.
   *
   * @param data       Report data DTO containing all required report info
   * @param outputPath Path to write the CSV file
   */
  public void exportReportAsCsv(ReportDataDTO data, String outputPath) {
    CSVFormat sectionFormat = CSVFormat.DEFAULT.builder().build(); // No header for section rows
    CSVFormat tableFormat = CSVFormat.DEFAULT.builder()
      .setHeader("Date", "Amount", "Direction", "Category", "Description", "Party", "Status")
      .build();

    try (FileWriter out = new FileWriter(outputPath);
         CSVPrinter sectionPrinter = new CSVPrinter(out, sectionFormat)) {

      // Section 1: Report metadata
      sectionPrinter.printRecord("Section", "Key", "Value");
      sectionPrinter.printRecord("REPORT", "Period", data.startDate() + " to " + data.endDate());
      sectionPrinter.printRecord("REPORT", "Transactions", data.transactionCount());
      sectionPrinter.printRecord("REPORT", "Report Generated", java.time.LocalDateTime.now());
      sectionPrinter.println();

      // Section 2: Aggregate totals
      sectionPrinter.printRecord("Section", "Key", "Value");
      sectionPrinter.printRecord("AGGREGATE", "Total Inflow", data.totalInflow());
      sectionPrinter.printRecord("AGGREGATE", "Total Outflow", data.totalOutflow());
      sectionPrinter.printRecord("AGGREGATE", "Net Balance", data.netBalance());
      sectionPrinter.printRecord("AGGREGATE", "Avg Daily Value", data.avgTransactionValue());
      sectionPrinter.println();

      // Section 3: Category breakdown
      sectionPrinter.printRecord("Section", "Key", "Value");
      for (var entry : data.categoryTotals().entrySet()) {
        sectionPrinter.printRecord("CATEGORY", entry.getKey(), entry.getValue());
      }
      sectionPrinter.println();

      // Section 4: Employee totals
      sectionPrinter.printRecord("Section", "Key", "Value");
      for (var entry : data.employeeTotals().entrySet()) {
        String name = employeeRepository.findById(entry.getKey()).getName();
        sectionPrinter.printRecord("EMPLOYEE", name, entry.getValue());
      }
      sectionPrinter.println();

      // Section 5: Counterparty totals
      sectionPrinter.printRecord("Section", "Key", "Value");
      for (var entry : data.counterpartyTotals().entrySet()) {
        String name = counterpartyRepository.findById(entry.getKey()).getName();
        sectionPrinter.printRecord("COUNTERPARTY", name, entry.getValue());
      }

      // Blank line before detailed transactions table
      sectionPrinter.println();

      // Section 6: Detailed transactions table with header
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

  /**
   * Builds a comprehensive HTML report string from the given report data.
   * Includes summaries, breakdowns, and transaction details formatted in tables.
   * Used as content for both HTML views and PDF exports.
   * @param data  the complete report data DTO
   * @param title report title to display
   * @return HTML string representing the full report
   */
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

    // Main report header and subheader with period
    html.append("<h2 style='text-align:center;margin-bottom:2px;'>").append(title).append("</h2>");
    html.append("<h4 style='text-align:center;margin-top:2px;'>")
      .append(data.startDate().getMonth()).append(" ").append(data.startDate().getYear())
      .append("</h4><hr/>");

    // Report metadata block
    html.append("<div>")
      .append("🗓 <b>PERIOD:</b> ").append(data.startDate()).append(" to ").append(data.endDate()).append("<br />")
      .append("📝 <b>TRANSACTIONS:</b> ").append(data.transactionCount()).append("<br />")
      .append("🕒 <b>REPORT GENERATED:</b> ").append(java.time.LocalDateTime.now()).append("<br />")
      .append("</div><hr/>");

    // Aggregate totals section
    html.append("<div class='section'>💰 AGGREGATE TOTALS</div>");
    html.append("<table>")
      .append("<tr><th>Total Inflow</th><th>Total Outflow</th><th>Net Balance</th><th>Avg Daily Value</th></tr>")
      .append("<tr>")
      .append("<td>₹").append(data.totalInflow()).append("</td>")
      .append("<td>₹").append(data.totalOutflow()).append("</td>")
      .append("<td>₹").append(data.netBalance()).append("</td>")
      .append("<td>₹").append(data.avgTransactionValue()).append("</td>")
      .append("</tr></table>");

    // Category breakdown
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

    // Entity breakdown: employees
    html.append("<div class='section'>👥 ENTITY BREAKDOWN</div>");
    html.append("<table><tr><th>EMPLOYEES</th><th>AMOUNT</th></tr>");
    data.employeeTotals().forEach((id, amt) -> {
      String name = employeeRepository.findById(id).getName();
      html.append("<tr><td>").append(name).append("</td><td>₹").append(amt).append("</td></tr>");
    });
    html.append("</table>");

    // Entity breakdown: counterparties
    html.append("<table><tr><th>COUNTERPARTIES</th><th>AMOUNT</th></tr>");
    data.counterpartyTotals().forEach((id, amt) -> {
      String name = counterpartyRepository.findById(id).getName();
      html.append("<tr><td>").append(name).append("</td><td>₹").append(amt).append("</td></tr>");
    });
    html.append("</table>");

    // Transactions detail table
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

  /**
   * Returns the internal ReportCalculator instance.
   * Useful for clients needing direct access to calculation methods.
   */
  public ReportCalculator getCalculator() {
    return calculator;
  }

  /**
   * Generates and returns the report data DTO for the given date range.
   * Delegates to the internal ReportCalculator.
   */
  public ReportDataDTO generateReportData(LocalDate start, LocalDate end) {
    return calculator.calculateReport(start, end);
  }

  /**
   * Prints the given report data to the console using the internal printer.
   * @param reportData the report DTO to be printed
   */
  public void printReport(ReportDataDTO reportData) {
    printer.printReport(reportData);
  }
}
