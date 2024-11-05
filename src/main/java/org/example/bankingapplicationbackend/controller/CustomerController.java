package org.example.bankingapplicationbackend.controller;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.bankingapplicationbackend.dto.*;
import org.example.bankingapplicationbackend.service.impl.CredentialsServiceImpl;
import org.example.bankingapplicationbackend.service.impl.bankStatementService.BankStatement;
import org.example.bankingapplicationbackend.service.impl.customerService.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@Tag(
        name = "Customers Account management APIs"
)
public class CustomerController {
    @Autowired
    CustomerService customerService;
    @Autowired
    CredentialsServiceImpl credentialsService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest)
    {
        return credentialsService.verify(loginRequest);
    }

    //GET METHODS
    @GetMapping("/balanceEnquiry")
    @Operation(
            summary = "Display Balance for a particular account",
            description = "Get balance for a particular account number by passing account number in the request"
    )
    @ApiResponse(
            responseCode = "302",
            description = "Balance FOUND"

    )
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest)
    {
        return customerService.balanceEnquiry(enquiryRequest);
    }

    @GetMapping("/nameEnquiry")
    @Operation(
            summary = "Display Name for a particular account",
            description = "Get Name of person for a particular account number by passing account number in the request"
    )
    @ApiResponse(
            responseCode = "302",
            description = "Name FOUND"

    )
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest)
    {
        return customerService.nameEnquiry(enquiryRequest);
    }

    @Operation(
            summary = "Get Transaction history",
            description = "Get list of all transactions done by a customers"
    )
    @ApiResponse(
            responseCode = "302",
            description = "FOUND"

    )
    @GetMapping("/getTransaction/{accountNumber}")
    public List<TransactionDTO> getTransactionsByUser(@PathVariable String accountNumber)
    {
        return customerService.getTransactionsByUser(accountNumber);
    }

    //POST METHODS

    @PostMapping("/makeTransaction")
    @Operation(
            summary = "Transfer amount",
            description = "Transfer amount from one account to another account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "SUCCESS"

    )
    public BankResponse makeTransaction(@RequestBody CreditDebitRequest request)
    {
        return customerService.makeTransaction(request);
    }


    @Autowired
    BankStatement bankStatement;
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
