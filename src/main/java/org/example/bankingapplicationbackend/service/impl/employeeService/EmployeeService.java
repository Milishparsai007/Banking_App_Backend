package org.example.bankingapplicationbackend.service.impl.employeeService;

import org.example.bankingapplicationbackend.dto.EmployeeDTO;
import org.example.bankingapplicationbackend.dto.EmployeeResponse;
import org.example.bankingapplicationbackend.entity.Employee;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface EmployeeService {
//    UserDetailsService userDetailService();

    EmployeeResponse addEmployee(Employee employee);
    List<EmployeeDTO> getAllEmployees();
}
