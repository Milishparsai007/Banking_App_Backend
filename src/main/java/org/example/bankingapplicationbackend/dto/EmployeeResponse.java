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
public class EmployeeResponse {
    private String responseCode;
    private String responseMessage;
    private EmployeeInfo employeeInfo;
//    private List<AccountInfo> accountInfoList;
}
