package org.example.bankingapplicationbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    @Schema(
            name = "Message"
    )
    private String message;
    @Schema
            (
                    name = "Account holder name"
            )
    private String accountName;
    @Schema(
            name = "Account Number"
    )
    private String accountNumber;
    @Schema(
            name = "Account balance"
    )
    private BigDecimal accountBalance;
}
