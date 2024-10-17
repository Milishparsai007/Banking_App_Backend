package org.example.bankingapplicationbackend.controller;

import org.example.bankingapplicationbackend.dto.BankResponse;
import org.example.bankingapplicationbackend.dto.CreditDebitRequest;
import org.example.bankingapplicationbackend.dto.EnquiryRequest;
import org.example.bankingapplicationbackend.dto.UserDTO;
import org.example.bankingapplicationbackend.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    //GET METHODS
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest)
    {
        return userService.balanceEnquiry(enquiryRequest);
    }

    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest)
    {
        return userService.nameEnquiry(enquiryRequest);
    }

    @GetMapping("/getAllUsers")
    public List<UserDTO> getAllUsers()
    {
        return userService.getAllUsers();
    }
    @GetMapping("/getUser/{accountNumber}")
    public BankResponse getUserByAccountNumber(@PathVariable String accountNumber)
    {
        return userService.getUserByAccountNumber(accountNumber);
    }

    //POST METHODS
    @PostMapping("/create-user")
    public BankResponse createAccount(@RequestBody UserDTO userDTO)

    {
        return userService.createAccount(userDTO);
    }

    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request)
    {
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request)
    {
        return userService.debitAccount(request);
    }

}
