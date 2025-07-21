package com.adhitya.paymgmt.service;

import com.adhitya.paymgmt.exception.EmptyResultException;
import com.adhitya.paymgmt.model.Counterparty;
import com.adhitya.paymgmt.model.enums.PartyType;
import com.adhitya.paymgmt.repository.CounterpartyRepository;

import java.util.List;

public class CounterpartyService {
  private final CounterpartyRepository counterpartyRepository;

  public CounterpartyService(CounterpartyRepository counterpartyRepository) {
    this.counterpartyRepository = counterpartyRepository;
  }

  public void addCounterparty(Counterparty counterparty) {
    if(counterparty == null) {
      throw new IllegalArgumentException("Counterparty cannot be null");
    }

    counterpartyRepository.save(counterparty);
  }

  public Counterparty findById(int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("Invalid counterparty ID: " + id);
    }

    Counterparty counterparty =  counterpartyRepository.findById(id);

    if(counterparty == null) {
      throw new EmptyResultException("No Counterparty Found for id: " + id);
    }

    return counterparty;
  }

  public List<Counterparty> getAll() {
    List<Counterparty> counterparties = counterpartyRepository.findAll();

    if(counterparties.isEmpty()) {
      throw new EmptyResultException("No Counterparties Found");
    }

    return counterparties;
  }

  public List<Counterparty> getByType(PartyType type) {
    if(type == null) {
      throw new IllegalArgumentException("Counterparty Type cannot be null");
    }

    List<Counterparty> counterparties = counterpartyRepository.findAllByType(type);

    if(counterparties.isEmpty()) {
      throw new EmptyResultException("No Counterparties Found for Counterparty Type: " + type);
    }

    return counterparties;
  }
}
