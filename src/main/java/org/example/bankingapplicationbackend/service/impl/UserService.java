package org.example.bankingapplicationbackend.service.impl;

import org.example.bankingapplicationbackend.dto.BankResponse;
import org.example.bankingapplicationbackend.dto.CreditDebitRequest;
import org.example.bankingapplicationbackend.dto.EnquiryRequest;
import org.example.bankingapplicationbackend.dto.UserDTO;

import java.util.List;

public interface UserService {
    BankResponse createAccount(UserDTO userDTO);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);

    List<UserDTO> getAllUsers();
    BankResponse getUserByAccountNumber(String accountNumber);
}
