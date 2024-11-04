package org.example.bankingapplicationbackend.dto;

import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private String transactionId;
    private String accountNum;
    private String transactionType;
    private BigDecimal amount;
    private String status;
    @CreationTimestamp
    private String createdAt;
    private String description;
    private BigDecimal remainingBalance;

}
