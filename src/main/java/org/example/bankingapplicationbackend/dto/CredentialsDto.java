package org.example.bankingapplicationbackend.dto;

import lombok.*;
import org.example.bankingapplicationbackend.entity.Role;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Data

public class CredentialsDto {
    private String userName;
    private String password;
    private Role role;
}
