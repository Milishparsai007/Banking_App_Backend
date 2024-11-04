package org.example.bankingapplicationbackend.service.impl.transactionService;

import org.example.bankingapplicationbackend.dto.TransactionDTO;
import org.example.bankingapplicationbackend.entity.Customers;

public interface TransactionService {
    void saveTransaction(TransactionDTO transaction, Customers customers);
}
