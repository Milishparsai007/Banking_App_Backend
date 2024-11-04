package org.example.bankingapplicationbackend.controller;

import org.example.bankingapplicationbackend.dto.EmployeeResponse;
import org.example.bankingapplicationbackend.entity.Employee;
import org.example.bankingapplicationbackend.service.impl.employeeService.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    @Autowired
    EmployeeServiceImpl employeeService;
    @PostMapping("/new-employee")
    public EmployeeResponse newEmployee(@RequestBody Employee employee)
    {
        return employeeService.addEmployee(employee);

    }
}
