package org.example.bankingapplicationbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder //used to create objects
@Entity
@Table(name = "Customers")
public class Customers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Customer Id")
    private long id;
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String state;
    private String accountNumber;//it will be generated on account creation
    private BigDecimal accountBalance;
    private String email;
    private String phoneNumber;
    private String alternateNumber;
    private String status;


    private String userName;
    private final String role=Role.CUSTOMER.toString();
    private String password;
    @CreationTimestamp


    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime modifiedAt;


    //list of transactions that the customers made.
    @OneToMany(mappedBy = "customers", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Transactions> transactionsList=new ArrayList<>();
}
