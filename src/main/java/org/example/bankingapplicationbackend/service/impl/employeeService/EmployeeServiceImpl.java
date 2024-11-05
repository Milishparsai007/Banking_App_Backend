package org.example.bankingapplicationbackend.service.impl.employeeService;

import org.example.bankingapplicationbackend.dto.*;
import org.example.bankingapplicationbackend.entity.Employee;
import org.example.bankingapplicationbackend.repository.EmployeeRepo;
import org.example.bankingapplicationbackend.service.impl.CredentialsServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    @Autowired
    EmployeeRepo employeeRepo;
    BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();

    ModelMapper modelMapper=new ModelMapper();

    @Autowired
    CredentialsServiceImpl credentialsService;

    public EmployeeResponse addEmployee(Employee employee)
    {
        //add employee in credentialsrepo
        CredentialsDto employeeCred=CredentialsDto.builder()
                .userName(employee.getUserName())
                .password(employee.getPassword())
                .role(employee.getRole())
                .build();
        credentialsService.addUser(employeeCred);

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

    @Override
    public List<EmployeeDTO> getAllEmployees()
    {
        List<Employee> employees=employeeRepo.findAll();
        List<EmployeeDTO> employeeDTOS=employees.stream().map((employee)->this.modelMapper.map(employee, EmployeeDTO.class)).collect(Collectors.toList());
        return employeeDTOS;
    }
}
