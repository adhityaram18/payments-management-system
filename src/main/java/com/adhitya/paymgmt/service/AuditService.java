package com.adhitya.paymgmt.service;

import com.adhitya.paymgmt.exception.EmptyResultException;
import com.adhitya.paymgmt.model.AuditLog;
import com.adhitya.paymgmt.repository.AuditLogRepository;

import java.util.List;

/** Service for handling audit log operations. */
public class AuditService {
  private final AuditLogRepository auditLogRepository;

  /** Creates service with the required repository. */
  public AuditService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  /** Records a new audit log entry. */
  public void logChange(AuditLog auditLog) {
    if(auditLog == null) {
      throw new IllegalArgumentException("AuditLog cannot be null");
    }

    auditLogRepository.save(auditLog);
  }

  /** Retrieves all audit logs. Throws if none found. */
  public List<AuditLog> getAllAuditLogs() {
    List<AuditLog> auditLogs = auditLogRepository.findAll();

    if(auditLogs.isEmpty()) {
      throw new EmptyResultException("No AuditLogs found");
    }

    return auditLogs;
  }

  /** Gets audit logs for a specific payment. Throws if invalid ID or none found. */
  public List<AuditLog> getLogsByPayment(int paymentId) {
    if(paymentId <= 0) {
      throw new IllegalArgumentException("Invalid payment ID: " + paymentId);
    }

    List<AuditLog> auditLogs = auditLogRepository.findByPaymentId(paymentId);

    if(auditLogs.isEmpty()) {
      throw new EmptyResultException("No AuditLogs found for payment ID: " + paymentId);
    }

    return auditLogs;
  }
}