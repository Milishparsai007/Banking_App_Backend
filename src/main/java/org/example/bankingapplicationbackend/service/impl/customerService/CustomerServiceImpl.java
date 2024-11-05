package org.example.bankingapplicationbackend.service.impl.customerService;

import org.example.bankingapplicationbackend.dto.*;
import org.example.bankingapplicationbackend.entity.Customers;
import org.example.bankingapplicationbackend.entity.Transactions;
import org.example.bankingapplicationbackend.repository.TransactionRepo;
import org.example.bankingapplicationbackend.repository.CustomerRepo;
import org.example.bankingapplicationbackend.service.impl.securityService.CredentialsServiceImpl;
import org.example.bankingapplicationbackend.service.impl.emailService.EmailService;
import org.example.bankingapplicationbackend.service.impl.transactionService.TransactionService;
import org.example.bankingapplicationbackend.utils.AccountUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;
    ModelMapper modelMapper=new ModelMapper();

    @Autowired
    CredentialsServiceImpl credentialsService;

    @Override
    public BankResponse createAccount(UserDTO userDTO) {
        //creating an account means saving a new customers in database.
        //check if customers already has an account

        if(customerRepo.existsByEmail(userDTO.getEmail()))
        {
            //return response to the customers which will be of type bank response
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        CredentialsDto customerCred=CredentialsDto.builder()
                .userName(userDTO.getUserName())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .build();
        credentialsService.addUser(customerCred);


        Customers newCustomers = Customers.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .otherName(userDTO.getOtherName())
                .state(userDTO.getState())
                .address(userDTO.getAddress())
                .accountNumber(AccountUtils.generateAccNumber())
                .accountBalance(BigDecimal.ZERO)
                .gender(userDTO.getGender())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .alternateNumber(userDTO.getAlternateNumber())
                .userName(userDTO.getUserName())
                .password(userDTO.getPassword())
                .status("ACTIVE") //status represents that this account is active, in processing or resrticted. We have created this for our own sake
                .build();
                 //this is used to build objects.

        Customers savedCustomers = customerRepo.save(newCustomers);

        //now send email alert to the customers of account creation
        EmailDetails emailDetails=EmailDetails.builder()
                .recipient(savedCustomers.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Bank Account has been created successfully.\nAccount Details :\n" +
                        "Account Name : "+ savedCustomers.getFirstName()+" "+ savedCustomers.getLastName()+" "+ savedCustomers.getOtherName()+"\n"+
                        "Account Number : "+ savedCustomers.getAccountNumber()+"\n"
                        )

                .build();
        emailService.sendEmail(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedCustomers.getAccountBalance())
                        .accountNumber(savedCustomers.getAccountNumber())
                        .accountName(savedCustomers.getFirstName()+" "+ savedCustomers.getLastName()+" "+ savedCustomers.getOtherName())
                        .build())
                .build();
    }

    //balance enquiry
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExist= customerRepo.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist)
        {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Customers foundCustomers = customerRepo.findCustomersByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundCustomers.getFirstName()+" "+ foundCustomers.getLastName()+" "+ foundCustomers.getOtherName())
                        .accountBalance(foundCustomers.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request)
    {
        boolean isAccountExist= customerRepo.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist)
        {
            return AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE;
        }

        Customers foundCustomers = customerRepo.findCustomersByAccountNumber(request.getAccountNumber());
        return foundCustomers.getFirstName()+" "+ foundCustomers.getLastName()+" "+ foundCustomers.getOtherName();
    }

    //CREDIT REQUESTS
    @Override
    public BankResponse makeTransaction(CreditDebitRequest request) {

        //base condition here will be that beneficiary and debitor account cannot be same.
        if(request.getBeneficiaryAccountNumber().equals(request.getDebitorAccountNumber()))
        {
            return BankResponse.builder()
                    .accountInfo(null)
                    .responseMessage(AccountUtils.SAME_ACCOUNT_TRANSACTION_ERROR)
                    .build();
        }


        boolean beneficiaryAccountExists = customerRepo.existsByAccountNumber(request.getBeneficiaryAccountNumber());
        boolean debitorAccountExists = customerRepo.existsByAccountNumber(request.getDebitorAccountNumber());
        if (!beneficiaryAccountExists && !debitorAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        //if debit party is from another bank then we don't need to check for balance of debit  party
        //this is CREDIT transaction to customers account which is in our bank
        if (!debitorAccountExists) {
            Customers creditedCustomers = customerRepo.findCustomersByAccountNumber(request.getBeneficiaryAccountNumber());
            creditedCustomers.setAccountBalance(creditedCustomers.getAccountBalance().add(request.getAmount()));
            customerRepo.save(creditedCustomers);

            //now save the transaction that is made above
            TransactionDTO transactionDTO=TransactionDTO.builder()
                    .transactionType("CREDIT")
                    .accountNum(creditedCustomers.getAccountNumber())
                    .amount(request.getAmount())
                    .remainingBalance(creditedCustomers.getAccountBalance())
                    .description("Account credited from "+request.getDebitorAccountNumber())
                    .build();
            transactionService.saveTransaction(transactionDTO, creditedCustomers);

            //now send email updates to the credited account which is from our bank.
            //we will format the senders account number in the form of XXXXXXX and last three digits of his acc num
            //same goes for the recievers acc number as this is the industry standard for mail sending and hiding
            //imp details like acc number in mail and sms.

            String sendersAccountNumber=request.getDebitorAccountNumber().toString().substring(0, request.getDebitorAccountNumber().toString().length() - 2).replaceAll("\\d", "X")
                    + "" + request.getDebitorAccountNumber().toString().substring(request.getDebitorAccountNumber().toString().length() - 3);

            String recieversAccountNumber=request.getBeneficiaryAccountNumber().toString().substring(0, request.getBeneficiaryAccountNumber().toString().length() - 2).replaceAll("\\d", "X")
                    + "" + request.getBeneficiaryAccountNumber().toString().substring(request.getBeneficiaryAccountNumber().toString().length() - 3);


            EmailDetails creditDetails = EmailDetails.builder()
                    .recipient(creditedCustomers.getEmail())
                    .subject("Alert! Account XXXXXXX" + creditedCustomers.getAccountNumber().toString().substring(7) + " CREDITED")
                    .messageBody("Rs "+request.getAmount()+" CREDITED to "+recieversAccountNumber+" from "+
                            sendersAccountNumber+"\nRemaining Balance : Rs. "+ creditedCustomers.getAccountBalance())
                    .build();
            emailService.sendEmail(creditDetails);

            List<AccountInfo> accountInfoList = new ArrayList<>();
            accountInfoList.add(AccountInfo.builder()
                    .message("Debited party details")
                    .accountNumber(request.getDebitorAccountNumber())
                    .build());
            accountInfoList.add(AccountInfo.builder()
                    .message("Beneficiary party details")
                    .accountNumber(creditedCustomers.getAccountNumber())
                    .accountName(creditedCustomers.getFirstName() + " " + creditedCustomers.getLastName() + " " + creditedCustomers.getOtherName())
                    .accountBalance(creditedCustomers.getAccountBalance())
                    .build());

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .message("Account Credited")
                            .accountNumber(creditedCustomers.getAccountNumber())
                            .accountName(creditedCustomers.getFirstName() + " " + creditedCustomers.getLastName() + " " + creditedCustomers.getOtherName())
                            .accountBalance(creditedCustomers.getAccountBalance())
                            .build())
                    .accountInfoList(accountInfoList)
                    .build();
        }

        //if credited party is from another bank then we need to check for balance of debit  party
        //this is DEBIT transaction to a customers which is in another bank from customers who is in our bank.
        if (!beneficiaryAccountExists) {
            Customers debitedCustomers = customerRepo.findCustomersByAccountNumber(request.getDebitorAccountNumber());
            BigInteger debitUserBalance = debitedCustomers.getAccountBalance().toBigInteger();
            BigInteger requestAmount = request.getAmount().toBigInteger();
            if (debitUserBalance.intValue()<requestAmount.intValue()) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountNumber(debitedCustomers.getAccountNumber())
                                .accountBalance(debitedCustomers.getAccountBalance())
                                .build())
                        .build();
            }

            debitedCustomers.setAccountBalance(debitedCustomers.getAccountBalance().subtract(request.getAmount()));
            customerRepo.save(debitedCustomers);

            //now save the transaction that is made above
            TransactionDTO transactionDTO=TransactionDTO.builder()
                    .transactionType("DEBIT")
                    .accountNum(debitedCustomers.getAccountNumber())
                    .amount(request.getAmount())
                    .remainingBalance(debitedCustomers.getAccountBalance())
                    .description("Account debited to "+request.getBeneficiaryAccountNumber())
                    .build();
            transactionService.saveTransaction(transactionDTO, debitedCustomers);

            //now send email updates to the credited account which is from our bank.
            //we will format the senders account number in the form of XXXXXXX and last three digits of his acc num
            //same goes for the recievers acc number as this is the industry standard for mail sending and hiding
            //imp details like acc number in mail and sms.

            String recieversAccountNumber=request.getBeneficiaryAccountNumber().toString().substring(0, request.getBeneficiaryAccountNumber().toString().length() - 2).replaceAll("\\d", "X")
                    + "" + request.getBeneficiaryAccountNumber().toString().substring(request.getBeneficiaryAccountNumber().toString().length() - 3);

            String sendersAccountNumber=request.getDebitorAccountNumber().toString()
                    .substring(0, request.getDebitorAccountNumber().toString().length() - 2)
                    .replaceAll("\\d", "X") + "" + request.getDebitorAccountNumber()
                    .toString().substring(request.getDebitorAccountNumber().toString().length() - 3);

            EmailDetails debitDetails = EmailDetails.builder()
                    .recipient(debitedCustomers.getEmail())
                    .subject("Alert! Account XXXXXXX" + debitedCustomers.getAccountNumber().toString().substring(7) + " DEBITED")
                    .messageBody("Rs "+request.getAmount()+"DEBITED from "+sendersAccountNumber+" to "
                    +recieversAccountNumber+"\nRemaining Balance : Rs. "+ debitedCustomers.getAccountBalance())
                    .build();
            emailService.sendEmail(debitDetails);

            List<AccountInfo> accountInfoList = new ArrayList<>();
            accountInfoList.add(AccountInfo.builder()
                    .message("Debited party details")
                    .accountNumber(debitedCustomers.getAccountNumber())
                    .accountName(debitedCustomers.getFirstName() + " " + debitedCustomers.getLastName() + " " + debitedCustomers.getOtherName())
                    .accountBalance(debitedCustomers.getAccountBalance())
                    .build());
            accountInfoList.add(AccountInfo.builder()
                    .message("Beneficiary party details")
                    .accountNumber(request.getBeneficiaryAccountNumber())
                    .build());

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .message("Account Debited")
                            .accountNumber(debitedCustomers.getAccountNumber())
                            .accountName(debitedCustomers.getFirstName() + " " + debitedCustomers.getLastName() + " " + debitedCustomers.getOtherName())
                            .accountBalance(debitedCustomers.getAccountBalance())
                            .build())
                    .accountInfoList(accountInfoList)
                    .build();
        }

        //if both parties are from our bank then we will first check that his/her acc balance is sufficient
        //enough to make the transaction or not.

        //EXAMPLE :- Mayank(debitor) transfers 1000 to Bhavya(creditor)
        else
        {
            Customers debitCustomers = customerRepo.findCustomersByAccountNumber(request.getDebitorAccountNumber());
            BigInteger debitUserBalance = debitCustomers.getAccountBalance().toBigInteger();
            BigInteger requestAmount = request.getAmount().toBigInteger();
            if (debitUserBalance.intValue()<requestAmount.intValue()) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountNumber(debitCustomers.getAccountNumber())
                                .accountBalance(debitCustomers.getAccountBalance())
                                .build())
                        .build();
            }


            Customers creditedCustomers = customerRepo.findCustomersByAccountNumber(request.getBeneficiaryAccountNumber());
            creditedCustomers.setAccountBalance(creditedCustomers.getAccountBalance().add(request.getAmount()));
            customerRepo.save(creditedCustomers);
            debitCustomers.setAccountBalance(debitCustomers.getAccountBalance().subtract(request.getAmount()));
            customerRepo.save(debitCustomers);

            //now save the transaction that is made above

            TransactionDTO transactionDebit=TransactionDTO.builder()
                    .transactionType("DEBIT")
                    .accountNum(debitCustomers.getAccountNumber())
                    .amount(request.getAmount())
                    .remainingBalance(debitCustomers.getAccountBalance())
                    .description("Account debited to "+ creditedCustomers.getAccountNumber())
                    .build();
            transactionService.saveTransaction(transactionDebit, debitCustomers);

            TransactionDTO transactionCredit=TransactionDTO.builder()
                    .transactionType("CREDIT")
                    .accountNum(creditedCustomers.getAccountNumber())
                    .description("Account credited from "+request.getDebitorAccountNumber())
                    .amount(request.getAmount())
                    .remainingBalance(creditedCustomers.getAccountBalance())
                    .build();
            transactionService.saveTransaction(transactionCredit, creditedCustomers);


            List<AccountInfo> accountInfoList = new ArrayList<>();
            accountInfoList.add(AccountInfo.builder()
                    .message("Debited party details")
                    .accountNumber(debitCustomers.getAccountNumber())
                    .accountName(debitCustomers.getFirstName() + " " + debitCustomers.getLastName() + " " + debitCustomers.getOtherName())
                    .accountBalance(debitCustomers.getAccountBalance())
                    .build());
            accountInfoList.add(AccountInfo.builder()
                    .message("Beneficiary party details")
                    .accountNumber(creditedCustomers.getAccountNumber())
                    .accountName(creditedCustomers.getFirstName() + " " + creditedCustomers.getLastName() + " " + creditedCustomers.getOtherName())
                    .accountBalance(creditedCustomers.getAccountBalance())
                    .build());

            //now send email alerts to both parties
            //1. CREDIT party
            String sendersAccountNumber=request.getDebitorAccountNumber().toString()
                    .substring(0, request.getDebitorAccountNumber().toString().length() - 2)
                    .replaceAll("\\d", "X") + "" + request.getDebitorAccountNumber()
                    .toString().substring(request.getDebitorAccountNumber().toString().length() - 3);

            String recieversAccountNumber=request.getBeneficiaryAccountNumber().toString().substring(0, request.getBeneficiaryAccountNumber().toString().length() - 2)
                    .replaceAll("\\d", "X") + "" + request.getBeneficiaryAccountNumber().toString()
                    .substring(request.getBeneficiaryAccountNumber().toString().length() - 3);

            EmailDetails emailDetailsCreditParty = EmailDetails.builder()
                    .recipient(creditedCustomers.getEmail())
                    .subject("Alert! Account XXXXXXX" + creditedCustomers.getAccountNumber().toString().substring(7) + " CREDITED")
                    .messageBody("Rs "+request.getAmount()+" CREDITED to "+recieversAccountNumber+" from "
                    +sendersAccountNumber+"\nRemaining Balance : Rs. "+ creditedCustomers.getAccountBalance())
                    .build();
            emailService.sendEmail(emailDetailsCreditParty);


            //2. DEBITED party
            EmailDetails emailDetailsDebitParty = EmailDetails.builder()
                    .recipient(debitCustomers.getEmail())
                    .subject("Alert! Account XXXXXXX" + debitCustomers.getAccountNumber().toString().substring(7) + " DEBITED")
                    .messageBody("Rs "+request.getAmount()+" DEBITED from "+sendersAccountNumber+" to "+
                            recieversAccountNumber+"\nRemaining balance : Rs. "+ debitCustomers.getAccountBalance())
                    .build();
            emailService.sendEmail(emailDetailsDebitParty);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE + " " + AccountUtils.ACCOUNT_CREDITED_CODE)
                    .responseMessage("The account "+ debitCustomers.getAccountNumber()+" "+AccountUtils.ACCOUNT_DEBITED_MESSAGE
                             + " The account " + creditedCustomers.getAccountNumber()+ " " + AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                    .accountInfoList(accountInfoList)
                    .build();
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<Customers> customers = customerRepo.findAll();
        List<UserDTO> userDTOS= customers.stream().map((user)->this.modelMapper.map(user,UserDTO.class)).toList();
        return userDTOS;
    }

    //GET ALL USERS/DISPLAY ALL USERS
    @Override
    public BankResponse getUserByAccountNumber(String accountNumber) {
        Customers customers = customerRepo.findCustomersByAccountNumber(accountNumber);
        boolean accountExists= customerRepo.existsByAccountNumber(accountNumber);
        if(!accountExists)
        {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .accountInfo(null)
                    .build();
        }
        return BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(accountNumber)
                        .accountName(customers.getFirstName()+" "+ customers.getLastName()+" "+ customers.getOtherName())
                        .accountBalance(customers.getAccountBalance())
                        .message("Details of account are : ")
                        .build())
                .build();
    }

    @Override
    public List<TransactionDTO> getTransactionsByUser(String accountNumber) {
        Customers customers = customerRepo.findCustomersByAccountNumber(accountNumber);
        List<Transactions> list=transactionRepo.findByCustomers(customers);

        List<TransactionDTO> transactionDTOList=list.stream().map((transactions)->this.modelMapper.map(transactions,TransactionDTO.class)).toList();
        return transactionDTOList;
    }


}
