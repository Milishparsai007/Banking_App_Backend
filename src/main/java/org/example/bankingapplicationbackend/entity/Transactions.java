package org.example.bankingapplicationbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    private String transactionType;
    private BigDecimal amount;
    private String status;
    @CreationTimestamp
    private LocalDate createdAt;
    private String description;
    @Column(name = "Account Number")
    private String accountNum;
    private BigDecimal remainingBalance;


    @ManyToOne
    @JoinColumn(name = "accountId")
    private Customers customers;
}
