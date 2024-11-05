package org.example.bankingapplicationbackend.dto;

import lombok.*;

@Data@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtAuthResponse {
    private String token;
}
