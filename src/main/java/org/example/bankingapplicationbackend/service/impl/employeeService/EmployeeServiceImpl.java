package org.example.bankingapplicationbackend.service.impl.employeeService;

import org.example.bankingapplicationbackend.dto.EmployeeInfo;
import org.example.bankingapplicationbackend.dto.EmployeeResponse;
import org.example.bankingapplicationbackend.entity.Employee;
import org.example.bankingapplicationbackend.repository.EmployeeRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    EmployeeRepo employeeRepo;
    ModelMapper modelMapper;

    BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    public EmployeeResponse addEmployee(Employee employee)
    {
        employee.setPassword(encoder.encode(employee.getPassword()));
        employeeRepo.save(employee);
        return EmployeeResponse.builder()
                .responseCode("001")
                .responseMessage("Employee Account Created")
                .employeeInfo(EmployeeInfo.builder()
                        .id(employee.getId().toString())
                        .userName(employee.getUserName())
                        .password(employee.getPassword())
                        .build())
                .build();
    }
}
