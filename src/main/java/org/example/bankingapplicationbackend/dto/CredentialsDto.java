package org.example.bankingapplicationbackend.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Data

public class CredentialsDto {
    private String userName;
    private String password;
    private String role;
}
