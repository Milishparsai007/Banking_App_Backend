package org.example.bankingapplicationbackend.service.impl.employeeService;

import org.example.bankingapplicationbackend.dto.EmployeeResponse;
import org.example.bankingapplicationbackend.entity.Employee;

public interface EmployeeService {
    EmployeeResponse addEmployee(Employee employee);
}
