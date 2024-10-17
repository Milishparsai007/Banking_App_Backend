package org.example.bankingapplicationbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDebitRequest {
    String beneficiaryAccountNumber;
    String debitorAccountNumber;
    BigDecimal amount;
}
