package com.adhitya.paymgmt.service;

import com.adhitya.paymgmt.exception.EmptyResultException;
import com.adhitya.paymgmt.model.AuditLog;
import com.adhitya.paymgmt.repository.AuditLogRepository;

import java.util.List;

public class AuditService {
  private final AuditLogRepository auditLogRepository;

  public AuditService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public void logChange(AuditLog auditLog) {
    if(auditLog == null) {
      throw new IllegalArgumentException("AuditLog cannot be null");
    }

    auditLogRepository.save(auditLog);
  }

  public List<AuditLog> getAllAuditLogs() {
    List<AuditLog> auditLogs = auditLogRepository.findAll();

    if(auditLogs.isEmpty()) {
      throw new EmptyResultException("No AuditLogs found");
    }

    return auditLogs;
  }

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
