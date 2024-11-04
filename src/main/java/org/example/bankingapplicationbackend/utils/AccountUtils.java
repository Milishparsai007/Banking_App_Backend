package org.example.bankingapplicationbackend.utils;

import java.time.Year;
import java.util.Date;

public class AccountUtils {

    //we will create custom status codes and messages for displaying the users.

    public static final String IFSC_Code="MPBA0005246";
    public static final String BANK_MAIL="milishspringboot@gmail.com";

    public static final String ACCOUNT_EXISTS_CODE="001";
    public static final String ACCOUNT_EXISTS_MESSAGE="Account already exists.";
    public static final String ACCOUNT_CREATION_CODE="002";
    public static final String ACCOUNT_CREATION_MESSAGE="Account Successfully created.";
    public static final String ACCOUNT_FOUND_CODE="003";
    public static final String ACCOUNT_FOUND_MESSAGE="Account found.";
    public static final String ACCOUNT_NOT_FOUND_CODE="004";
    public static final String ACCOUNT_NOT_FOUND_MESSAGE="Account does not exist. Please fill correct account number.";
    public static final String SAME_ACCOUNT_TRANSACTION_ERROR="Invalid Transaction!! Beneficiary account and Debitor account cannot be same!";
    public static final String ACCOUNT_CREDITED_CODE="005";
    public static final String ACCOUNT_CREDITED_MESSAGE="has been credited";
    public static final String INSUFFICIENT_BALANCE_CODE="006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE="Insufficient balance in your account.";
    public static final String ACCOUNT_DEBITED_CODE="007";
    public static final String ACCOUNT_DEBITED_MESSAGE="has been debited";
    public static final String TRANSFER_SUCCESSFUL_CODE="008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE="Transfer successful";


    public static final String ACCOUNT_CREDITED_EMAIL="Account has been credited with";
    public static final String ACCOUNT_DEBITED_EMAIL="Account has been debited with";


    public static String generateAccNumber()
    {
        //acc number will be :- year+six digit random number
        Year currentYear=Year.now();
        int min=100000;
        int max=999999;
        int randomNumber=(int)Math.floor(Math.random()*(max-min+1)+min);
        String str=String.valueOf(randomNumber);
        String year=String.valueOf(currentYear);
        StringBuilder accountNumber=new StringBuilder();
        return accountNumber.append(year).append(str).toString();
    }
}
