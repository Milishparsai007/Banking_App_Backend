package org.example.bankingapplicationbackend.controller;

import com.itextpdf.text.DocumentException;
import org.example.bankingapplicationbackend.dto.TransactionDTO;
import org.example.bankingapplicationbackend.service.impl.bankStatementService.BankStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
public class TransactionController {
    @Autowired
    BankStatement bankStatement;
    @GetMapping
    public List<TransactionDTO> generateBankStatement(@RequestParam String accountNumber,
                                                      @RequestParam String startDate,
                                                      @RequestParam String endDate)
    {
        return bankStatement.generateBankStatement(accountNumber,startDate,endDate);
    }

    @GetMapping("/sendEmail")
    public List<TransactionDTO> sendBankStatementToEmail(@RequestParam String accountNumber,
                                                      @RequestParam String startDate,
                                                      @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatement.sendBankStatementToEmail(accountNumber,startDate,endDate);
    }
}
