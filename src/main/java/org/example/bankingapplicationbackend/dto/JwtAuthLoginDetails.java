package org.example.bankingapplicationbackend.dto;

import lombok.*;
import org.example.bankingapplicationbackend.entity.Role;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class JwtAuthLoginDetails {
    private String username;
    private Role role;
}
