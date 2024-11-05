package org.example.bankingapplicationbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Table(name = "Employees")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Employee Id")
    private Long id;
    @Column(name = "Employee Name",nullable = false)
    private String userName;
    private final Role role=Role.EMPLOYEE;
    @Column(name = "Password",nullable = false)
    private String password;
}
