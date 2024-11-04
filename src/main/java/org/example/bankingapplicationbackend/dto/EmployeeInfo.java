package org.example.bankingapplicationbackend.dto;

import lombok.*;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeInfo {
    private String id;
    private String userName;
    private String password;
}
