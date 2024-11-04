package org.example.bankingapplicationbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.bankingapplicationbackend.dto.*;
import org.example.bankingapplicationbackend.service.impl.customerService.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@Tag(
        name = "Customers Account management APIs"
)
public class CustomerController {
    @Autowired
    CustomerService customerService;

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

    @GetMapping("/getAllUsers")
    @Operation(
            summary = "Get all account",
            description = "Display all accounts present in DB"
    )
    @ApiResponse(
            responseCode = "302",
            description = "FOUND"

    )
    public List<UserDTO> getAllUsers()
    {
        return customerService.getAllUsers();
    }

    @Operation(
            summary = "Display Customers details for a particular account",
            description = "Get Customers details for a particular account number by passing account number in the request"
    )
    @ApiResponse(
            responseCode = "302",
            description = "Customers FOUND"

    )
    @GetMapping("/getUser/{accountNumber}")
    public BankResponse getUserByAccountNumber(@PathVariable String accountNumber)
    {
        return customerService.getUserByAccountNumber(accountNumber);
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
    @Operation(
            summary = "Create new Customers",
            description = "Creating new customers and assigning a unique account id which is account number"
    )
    @ApiResponse(
            responseCode = "201",
            description = "CREATED"

    )
    @PostMapping("/create-user")
    public BankResponse createAccount(@RequestBody UserDTO userDTO)

    {
        return customerService.createAccount(userDTO);
    }

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




}
