package org.example.bankingapplicationbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankResponse {
    private String responseCode;
    private String responseMessage;
    private AccountInfo accountInfo;
    private List<AccountInfo> accountInfoList;
}
