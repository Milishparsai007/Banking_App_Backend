package org.example.bankingapplicationbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String state;
    private String email;
    private String phoneNumber;
    private String alternateNumber;
    private String status;
    private BigDecimal accountBalance;
    private String userName;
    private String password;
    private String role;
}
