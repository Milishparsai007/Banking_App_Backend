package org.example.bankingapplicationbackend.service.impl.customerService;

import org.example.bankingapplicationbackend.dto.*;

import java.util.List;

public interface CustomerService {
    BankResponse createAccount(UserDTO userDTO);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);

    BankResponse makeTransaction(CreditDebitRequest request);

    List<UserDTO> getAllUsers();
    BankResponse getUserByAccountNumber(String accountNumber);

    List<TransactionDTO> getTransactionsByUser(String accountNumber);
}
