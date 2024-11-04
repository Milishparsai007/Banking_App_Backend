package org.example.bankingapplicationbackend.service.impl.transactionService;


import org.example.bankingapplicationbackend.dto.TransactionDTO;
import org.example.bankingapplicationbackend.entity.Customers;
import org.example.bankingapplicationbackend.entity.Transactions;
import org.example.bankingapplicationbackend.repository.TransactionRepo;
import org.example.bankingapplicationbackend.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    CustomerRepo customerRepo;
    @Override
    public void saveTransaction(TransactionDTO transactionDto, Customers customers) {


        Transactions transactions=Transactions.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNum(transactionDto.getAccountNum())
                .description(transactionDto.getDescription())
                .amount(transactionDto.getAmount())
                .status("SUCCESSFUL")
                .remainingBalance(transactionDto.getRemainingBalance())
                .build();
        transactions.setCustomers(customers);

        transactionRepo.save(transactions);
        System.out.println("Transaction saved successfully");
    }
}
