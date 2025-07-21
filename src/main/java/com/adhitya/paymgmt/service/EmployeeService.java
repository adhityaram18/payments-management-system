package com.adhitya.paymgmt.service;

import com.adhitya.paymgmt.exception.EmptyResultException;
import com.adhitya.paymgmt.model.Employee;
import com.adhitya.paymgmt.repository.EmployeeRepository;

import java.util.List;

public class EmployeeService {
  private final EmployeeRepository employeeRepository;

  public EmployeeService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  public void addEmployee(Employee employee) {
    if(employee == null) {
      throw new IllegalArgumentException("Employee cannot be null");
    }

    employeeRepository.save(employee);
  }

  public Employee findById(int id) {
    if(id <= 0) {
      throw new IllegalArgumentException("Invalid employee ID: " + id);
    }

    Employee employee = employeeRepository.findById(id);

    if(employee == null) {
      throw new EmptyResultException("No Employee Found for employee ID: " + id);
    }

    return employee;
  }

  public List<Employee> getAll() {
    List<Employee> employees = employeeRepository.findAll();

    if(employees.isEmpty()) {
      throw new EmptyResultException("No Employees Found");
    }

    return employees;
  }
}
