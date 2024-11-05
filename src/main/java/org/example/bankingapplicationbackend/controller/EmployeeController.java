package org.example.bankingapplicationbackend.controller;

import com.itextpdf.text.DocumentException;
import org.example.bankingapplicationbackend.dto.*;
import org.example.bankingapplicationbackend.entity.Employee;
import org.example.bankingapplicationbackend.service.impl.CredentialsServiceImpl;
import org.example.bankingapplicationbackend.service.impl.bankStatementService.BankStatement;
import org.example.bankingapplicationbackend.service.impl.customerService.CustomerServiceImpl;
import org.example.bankingapplicationbackend.service.impl.employeeService.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    EmployeeServiceImpl employeeService;
    @Autowired
    CustomerServiceImpl customerService;


    @Autowired
    CredentialsServiceImpl credentialsService;

    @Autowired
    BankStatement bankStatement;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest)
    {
        return credentialsService.verify(loginRequest);
    }

    @PostMapping("/new-employee")
    public EmployeeResponse newEmployee(@RequestBody Employee employee)
    {
        return employeeService.addEmployee(employee);

    }

    @PostMapping("/create-user")
    public BankResponse createAccount(@RequestBody UserDTO userDTO)

    {
        return customerService.createAccount(userDTO);
    }

    @GetMapping("/get-employees")
    public List<EmployeeDTO> getAllEmployees()
    {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/get-all-users")
    public List<UserDTO> getAllUsers()
    {
        return customerService.getAllUsers();
    }

    @GetMapping("/getUser/{accountNumber}")
    public BankResponse getUserByAccountNumber(@PathVariable String accountNumber)
    {
        return customerService.getUserByAccountNumber(accountNumber);
    }
    @GetMapping("/statement")
    public List<TransactionDTO> generateBankStatement(@RequestParam String accountNumber,
                                                      @RequestParam String startDate,
                                                      @RequestParam String endDate)
    {
        return bankStatement.generateBankStatement(accountNumber,startDate,endDate);
    }

    @GetMapping("/statement/sendEmail")
    public List<TransactionDTO> sendBankStatementToEmail(@RequestParam String accountNumber,
                                                         @RequestParam String startDate,
                                                         @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatement.sendBankStatementToEmail(accountNumber,startDate,endDate);
    }
}
