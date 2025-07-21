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

public class PaymentService {
  private final PaymentRepository paymentRepository;
  private final AuditLogRepository auditLogRepository;
  private final UserRepository userRepository;

  public PaymentService(PaymentRepository paymentRepository, AuditLogRepository auditLogRepository, UserRepository userRepository) {
    this.paymentRepository = paymentRepository;
    this.auditLogRepository = auditLogRepository;
    this.userRepository = userRepository;
  }

  public void addPayment(Payment payment) {
    if (payment == null) {
      throw new IllegalArgumentException("Payment cannot be null");
    }

    paymentRepository.save(payment);
  }

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

    paymentRepository.updateStatus(paymentId,newStatus);

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

  public List<Payment> getAllPayments() {
    List<Payment> payments = paymentRepository.findAll();

    if(payments.isEmpty()) {
      throw new EmptyResultException("No Payments Found Yet");
    }

    return payments;
  }

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

  public List<Payment> getPaymentsByDateRange(LocalDate start, LocalDate end) {
    if(start == null || end == null) {
      throw new IllegalArgumentException("Start/end dates cannot be null");
    }
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("End date must be after start date");
    }

    List<Payment> payments = paymentRepository.findByDateRange(start,end);

    if(payments.isEmpty()) {
      throw new EmptyResultException("No payments found between " + start + " and " + end);
    }

    return payments;
  }

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
