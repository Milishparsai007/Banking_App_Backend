package org.example.bankingapplicationbackend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginRequest {
    private String userName;
    private String password;
}
