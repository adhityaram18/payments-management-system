package com.adhitya.paymgmt.service;

import com.adhitya.paymgmt.exception.EmptyResultException;
import com.adhitya.paymgmt.model.AuditLog;
import com.adhitya.paymgmt.model.Payment;
import com.adhitya.paymgmt.model.enums.PaymentCategory;
import com.adhitya.paymgmt.model.enums.PaymentDirection;
import com.adhitya.paymgmt.model.enums.Status;
import com.adhitya.paymgmt.repository.AuditLogRepository;
import com.adhitya.paymgmt.repository.PaymentRepository;
import com.adhitya.paymgmt.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class responsible for managing Payment operations,
 * including creation, status updates, and data retrieval.
 * Also handles audit logging on status changes.
 */
public class PaymentService {
  private final PaymentRepository paymentRepository;
  private final AuditLogRepository auditLogRepository;
  private final UserRepository userRepository;

  /**
   * Constructs PaymentService with required repository dependencies.
   *
   * @param paymentRepository  repository for payment persistence
   * @param auditLogRepository repository for audit log persistence
   * @param userRepository     repository to retrieve user info for audit logging
   */
  public PaymentService(PaymentRepository paymentRepository, AuditLogRepository auditLogRepository, UserRepository userRepository) {
    this.paymentRepository = paymentRepository;
    this.auditLogRepository = auditLogRepository;
    this.userRepository = userRepository;
  }

  /**
   * Adds a new Payment record after validating non-nullity.
   * @param payment payment object to save
   * @throws IllegalArgumentException if payment is null
   */
  public void addPayment(Payment payment) {
    if (payment == null) {
      throw new IllegalArgumentException("Payment cannot be null");
    }

    paymentRepository.save(payment);
  }

  /**
   * Updates the status of an existing payment and records the change in audit logs.
   * Validates inputs for IDs and status before processing.
   *
   * @param paymentId      ID of payment to update
   * @param newStatus      new status to set
   * @param changedByUserId ID of user making the status change
   * @throws IllegalArgumentException for invalid IDs or null status
   * @throws EmptyResultException     if payment is not found
   */
  public void updatePaymentStatus(int paymentId, Status newStatus, int changedByUserId) {
    if (paymentId <= 0 || changedByUserId <= 0) {
      throw new IllegalArgumentException("Invalid ID(s) provided");
    }
    if (newStatus == null) {
      throw new IllegalArgumentException("Payment status cannot be null");
    }

    Payment oldPayment = paymentRepository.findById(paymentId);

    if (oldPayment == null) {
      throw new EmptyResultException("Payment not found with ID: " + paymentId);
    }

    // Update payment status in repository
    paymentRepository.updateStatus(paymentId, newStatus);

    // Record audit log capturing the status change
    auditLogRepository.save(new AuditLog(
      0,
      oldPayment,
      "status",
      oldPayment.getStatus().getDbValue(),
      newStatus.getDbValue(),
      userRepository.findById(changedByUserId),
      LocalDateTime.now()
    ));
  }

  /**
   * Finds and returns a Payment by its ID.
   * @param id payment ID
   * @return Payment object
   * @throws IllegalArgumentException if ID is invalid
   * @throws EmptyResultException     if payment not found
   */
  public Payment findById(int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("Invalid payment ID: " + id);
    }

    Payment payment = paymentRepository.findById(id);

    if(payment == null) {
      throw new EmptyResultException("Payment not found with ID: " + id);
    }

    return payment;
  }

  /**
   * Retrieves all payments in the system.
   * @return list of all payments
   * @throws EmptyResultException if no payments are present
   */
  public List<Payment> getAllPayments() {
    List<Payment> payments = paymentRepository.findAll();

    if(payments.isEmpty()) {
      throw new EmptyResultException("No Payments Found Yet");
    }

    return payments;
  }

  /**
   * Retrieves payments created by a specific user.
   * @param userId ID of the user
   * @return list of payments by user
   * @throws IllegalArgumentException if userId invalid
   * @throws EmptyResultException     if no payments found for user
   */
  public List<Payment> getPaymentsByUser(int userId) {
    if(userId <= 0) {
      throw new IllegalArgumentException("Invalid user ID: " + userId);
    }

    List<Payment> payments = paymentRepository.findByUserId(userId);

    if(payments.isEmpty()) {
      throw new EmptyResultException("No Payments Found for userId: " + userId);
    }

    return payments;
  }

  /**
   * Retrieves payments within a date range (inclusive).
   * Validates date inputs and ensures start <= end.
   * @param start inclusive start date
   * @param end   inclusive end date
   * @return list of payments in date range
   * @throws IllegalArgumentException for invalid/null dates or start > end
   * @throws EmptyResultException     if none found
   */
  public List<Payment> getPaymentsByDateRange(LocalDate start, LocalDate end) {
    if(start == null || end == null) {
      throw new IllegalArgumentException("Start/end dates cannot be null");
    }
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("End date must be after start date");
    }

    List<Payment> payments = paymentRepository.findByDateRange(start, end);

    if(payments.isEmpty()) {
      throw new EmptyResultException("No payments found between " + start + " and " + end);
    }

    return payments;
  }

  /**
   * Retrieves payments filtered by payment category.
   * @param category PaymentCategory enum value
   * @return list of payments matching category
   * @throws IllegalArgumentException if category is null
   * @throws EmptyResultException     if none found
   */
  public List<Payment> getPaymentsByCategory(PaymentCategory category) {
    if(category == null) {
      throw new IllegalArgumentException("Payment Category cannot be null");
    }

    List<Payment> payments = paymentRepository.findByCategory(category);

    if(payments.isEmpty()) {
      throw new EmptyResultException("No payments found for category: " + category.name());
    }

    return payments;
  }

  /**
   * Retrieves payments filtered by payment direction (INCOMING or OUTGOING).
   * @param direction PaymentDirection enum value
   * @return list of payments filtered by direction
   * @throws IllegalArgumentException if direction is null
   * @throws EmptyResultException     if no payments found
   */
  public List<Payment> getPaymentsByPaymentDirection(PaymentDirection direction) {
    if(direction == null) {
      throw new IllegalArgumentException("Payment Direction cannot be null");
    }

    List<Payment> payments = paymentRepository.findByDirection(direction);

    if(payments.isEmpty()) {
      throw new EmptyResultException("No payments found for Payment Direction: " + direction.name());
    }

    return payments;
  }

  /**
   * Retrieves payments associated with a given counterparty.
   * @param counterpartyId ID of the counterparty
   * @return list of payments related to that counterparty
   * @throws IllegalArgumentException if counterparty ID invalid
   * @throws EmptyResultException     if none found
   */
  public List<Payment> getPaymentsByCounterparty(int counterpartyId) {
    if(counterpartyId <= 0) {
      throw new IllegalArgumentException("Invalid counterparty ID: " + counterpartyId);
    }

    List<Payment> payments = paymentRepository.findByCounterpartyId(counterpartyId);

    if(payments.isEmpty()) {
      throw new EmptyResultException("No payments found for counterparty ID: " + counterpartyId);
    }

    return payments;
  }

  /**
   * Retrieves payments associated with a given employee.
   * @param employeeId ID of the employee
   * @return list of payments related to that employee
   * @throws IllegalArgumentException if employee ID invalid
   * @throws EmptyResultException     if none found
   */
  public List<Payment> getPaymentsByEmployee(int employeeId) {
    if(employeeId <= 0) {
      throw new IllegalArgumentException("Invalid employee ID: " + employeeId);
    }

    List<Payment> payments = paymentRepository.findByEmployeeId(employeeId);

    if(payments.isEmpty()) {
      throw new EmptyResultException("No payments found for employee Id: " + employeeId);
    }

    return payments;
  }
}
