package org.example.bankingapplicationbackend.service.impl;

import org.example.bankingapplicationbackend.dto.*;
import org.example.bankingapplicationbackend.entity.User;
import org.example.bankingapplicationbackend.repository.UserRepo;
import org.example.bankingapplicationbackend.utils.AccountUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepo userRepo;

    @Autowired
    EmailService emailService;
    ModelMapper modelMapper=new ModelMapper();

    @Override
    public BankResponse createAccount(UserDTO userDTO) {
        //creating an account means saving a new user in database.
        //check if user already has an account

        if(userRepo.existsByEmail(userDTO.getEmail()))
        {
            //return response to the user which will be of type bank response
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser= User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .otherName(userDTO.getOtherName())
                .state(userDTO.getState())
                .accountNumber(AccountUtils.generateAccNumber())
                .accountBalance(BigDecimal.ZERO)
                .gender(userDTO.getGender())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .alternateNumber(userDTO.getAlternateNumber())
                .status("ACTIVE") //status represents that this account is active, in processing or resrticted. We have created this for our own sake
                .build();
                 //this is used to build objects.

        User savedUser=userRepo.save(newUser);

        //now send email alert to the user of account creation
        EmailDetails emailDetails=EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Bank Account has been created successfully.\nAccount Details :\n" +
                        "Account Name : "+savedUser.getFirstName()+" "+savedUser.getLastName()+" "+savedUser.getOtherName()+"\n"+
                        "Account Number : "+savedUser.getAccountNumber()+"\n"
                        )

                .build();
        emailService.sendEmail(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName()+" "+savedUser.getLastName()+" "+savedUser.getOtherName())
                        .build())
                .build();
    }

    //balance enquiry
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExist= userRepo.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist)
        {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser=userRepo.findUserByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName())
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request)
    {
        boolean isAccountExist= userRepo.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist)
        {
            return AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE;
        }

        User foundUser=userRepo.findUserByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName();
    }

    //CREDIT REQUESTS
    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //base condition here will be that beneficiary and debitor account cannot be same.
        if(request.getBeneficiaryAccountNumber().equals(request.getDebitorAccountNumber()))
        {
            return BankResponse.builder()
                    .accountInfo(null)
                    .responseMessage(AccountUtils.SAME_ACCOUNT_TRANSACTION_ERROR)
                    .build();
        }


        boolean beneficiaryAccountExists = userRepo.existsByAccountNumber(request.getBeneficiaryAccountNumber());
        boolean debitorAccountExists = userRepo.existsByAccountNumber(request.getDebitorAccountNumber());
        if (!beneficiaryAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        //if debit party is from another bank then we don't need to check for balance of debit  party
        if (!debitorAccountExists) {
            User creditedUser = userRepo.findUserByAccountNumber(request.getBeneficiaryAccountNumber());
            creditedUser.setAccountBalance(creditedUser.getAccountBalance().add(request.getAmount()));
            userRepo.save(creditedUser);

            //now send email updates to the credited account which is from our bank.
            //we will format the senders account number in the form of XXXXXXX and last three digits of his acc num
            //same goes for the recievers acc number as this is the industry standard for mail sending and hiding
            //imp details like acc number in mail and sms.

            String sendersAccountNumber=request.getDebitorAccountNumber().toString().substring(0, request.getDebitorAccountNumber().toString().length() - 2).replaceAll("\\d", "X")
                    + "" + request.getDebitorAccountNumber().toString().substring(request.getDebitorAccountNumber().toString().length() - 3);

            EmailDetails creditDetails = EmailDetails.builder()
                    .recipient(creditedUser.getEmail())
                    .subject("Account XXXXXXX" + creditedUser.getAccountNumber().toString().substring(7) + " credited")
                    .messageBody(AccountUtils.ACCOUNT_CREDITED_EMAIL + " " + request.getAmount() + " from "+sendersAccountNumber

                            + "\n" + "Remaining balance is : " + creditedUser.getAccountBalance()
                    )
                    .build();
            emailService.sendEmail(creditDetails);

            List<AccountInfo> accountInfoList = new ArrayList<>();
            accountInfoList.add(AccountInfo.builder()
                    .message("Debited party details")
                    .accountNumber(request.getDebitorAccountNumber())
                    .build());
            accountInfoList.add(AccountInfo.builder()
                    .message("Beneficiary party details")
                    .accountNumber(creditedUser.getAccountNumber())
                    .accountName(creditedUser.getFirstName() + " " + creditedUser.getLastName() + " " + creditedUser.getOtherName())
                    .accountBalance(creditedUser.getAccountBalance())
                    .build());

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .message("Account Credited")
                            .accountNumber(creditedUser.getAccountNumber())
                            .accountName(creditedUser.getFirstName() + " " + creditedUser.getLastName() + " " + creditedUser.getOtherName())
                            .accountBalance(creditedUser.getAccountBalance())
                            .build())
                    .accountInfoList(accountInfoList)
                    .build();
        }

        //if debited party is from our bank then we will first check that his/her acc balance is sufficient
        //enough to make the transaction or not.
        else
        {
            User debitUser = userRepo.findUserByAccountNumber(request.getDebitorAccountNumber());
            BigInteger debitUserBalance = debitUser.getAccountBalance().toBigInteger();
            BigInteger requestAmount = request.getAmount().toBigInteger();
            if (debitUserBalance.intValue()<requestAmount.intValue()) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountNumber(debitUser.getAccountNumber())
                                .accountBalance(debitUser.getAccountBalance())
                                .build())
                        .build();
            }


            User creditedUser = userRepo.findUserByAccountNumber(request.getBeneficiaryAccountNumber());
            creditedUser.setAccountBalance(creditedUser.getAccountBalance().add(request.getAmount()));
            userRepo.save(creditedUser);
            debitUser.setAccountBalance(debitUser.getAccountBalance().subtract(request.getAmount()));
            userRepo.save(debitUser);
            List<AccountInfo> accountInfoList = new ArrayList<>();
            accountInfoList.add(AccountInfo.builder()
                    .message("Debited party details")
                    .accountNumber(debitUser.getAccountNumber())
                    .accountName(debitUser.getFirstName() + " " + debitUser.getLastName() + " " + debitUser.getOtherName())
                    .accountBalance(debitUser.getAccountBalance())
                    .build());
            accountInfoList.add(AccountInfo.builder()
                    .message("Beneficiary party details")
                    .accountNumber(creditedUser.getAccountNumber())
                    .accountName(creditedUser.getFirstName() + " " + creditedUser.getLastName() + " " + creditedUser.getOtherName())
                    .accountBalance(creditedUser.getAccountBalance())
                    .build());

            //now send email alerts to both parties
            String senderAccountNumber=request.getDebitorAccountNumber().toString()
                    .substring(0, request.getDebitorAccountNumber().toString().length() - 2)
                    .replaceAll("\\d", "X") + "" + request.getDebitorAccountNumber()
                    .toString().substring(request.getDebitorAccountNumber().toString().length() - 3);

            EmailDetails emailDetailsCreditParty = EmailDetails.builder()
                    .recipient(creditedUser.getEmail())
                    .subject("Account XXXXXXX" + creditedUser.getAccountNumber().toString().substring(7) + " credited")
                    .messageBody(AccountUtils.ACCOUNT_CREDITED_EMAIL + " " + request.getAmount() +
                            " from " + senderAccountNumber
                            + "\n" + "Remaining balance is : " + creditedUser.getAccountBalance()
                    )
                    .build();
            emailService.sendEmail(emailDetailsCreditParty);

            String recieverAccountNumber=request.getBeneficiaryAccountNumber().toString().substring(0, request.getBeneficiaryAccountNumber().toString().length() - 2)
                    .replaceAll("\\d", "X") + "" + request.getBeneficiaryAccountNumber().toString()
                    .substring(request.getBeneficiaryAccountNumber().toString().length() - 3);
            EmailDetails emailDetailsDebitParty = EmailDetails.builder()
                    .recipient(debitUser.getEmail())
                    .subject("Account XXXXXXX" + debitUser.getAccountNumber().toString().substring(7) + " debited")
                    .messageBody(AccountUtils.ACCOUNT_DEBITED_EMAIL + " " + request.getAmount() + " to "
                            + recieverAccountNumber
                            + "\n" + "Remaining balance is : " + debitUser.getAccountBalance()
                    )
                    .build();
            emailService.sendEmail(emailDetailsDebitParty);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE + " " + AccountUtils.ACCOUNT_CREDITED_CODE)
                    .responseMessage("The account "+debitUser.getAccountNumber()+" "+AccountUtils.ACCOUNT_DEBITED_MESSAGE
                             + " The account " +creditedUser.getAccountNumber()+ " " + AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                    .accountInfoList(accountInfoList)
                    .build();
        }
    }


    //DEBIT REQUESTS
    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {

        //base condition here will be that beneficiary and debitor account cannot be same.
        if(request.getBeneficiaryAccountNumber().equals(request.getDebitorAccountNumber()))
        {
            return BankResponse.builder()
                    .accountInfo(null)
                    .responseMessage(AccountUtils.SAME_ACCOUNT_TRANSACTION_ERROR)
                    .build();
        }


        boolean beneficiaryAccountExists = userRepo.existsByAccountNumber(request.getBeneficiaryAccountNumber());
        boolean debitorAccountExists = userRepo.existsByAccountNumber(request.getDebitorAccountNumber());
        if(!debitorAccountExists)
        {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        //if beneficiary account is not in our bank then no need to worry about its details.
        //just debit the debitor account
        if (!beneficiaryAccountExists)
        {
            User debitUser = userRepo.findUserByAccountNumber(request.getDebitorAccountNumber());
            debitUser.setAccountBalance(debitUser.getAccountBalance().subtract(request.getAmount()));
            userRepo.save(debitUser);

            //now send email updates to the credited account which is from our bank.
            String recieverAccountNumber=request.getBeneficiaryAccountNumber().toString().substring(0, request.getBeneficiaryAccountNumber().toString().length() - 2).replaceAll("\\d", "X")
                    + "" + request.getBeneficiaryAccountNumber().toString().substring(request.getBeneficiaryAccountNumber().toString().length() - 3);
            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(debitUser.getEmail())
                    .subject("Account XXXXXXX" + debitUser.getAccountNumber().toString().substring(7) + " debited")
                    .messageBody(AccountUtils.ACCOUNT_DEBITED_EMAIL + " " + request.getAmount() + " to "
                            + recieverAccountNumber
                            + "\n" + "Remaining balance is : " + debitUser.getAccountBalance()
                    )
                    .build();
            emailService.sendEmail(emailDetails);

            List<AccountInfo> accountInfoList = new ArrayList<>();
            accountInfoList.add(AccountInfo.builder()
                    .message("Debited party details")
                    .accountNumber(debitUser.getAccountNumber())
                    .accountName(debitUser.getFirstName()+" "+debitUser.getLastName()+" "+debitUser.getOtherName())
                    .accountBalance(debitUser.getAccountBalance())
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
                            .accountNumber(debitUser.getAccountNumber())
                            .accountName(debitUser.getFirstName() + " " + debitUser.getLastName() + " " + debitUser.getOtherName())
                            .accountBalance(debitUser.getAccountBalance())
                            .build())
                    .accountInfoList(accountInfoList)
                    .build();
        }

        //if both accounts lie in our bank the check for debit account balance and request balance
        //if low balance then no transaction happens
        else
        {
            User debitUser = userRepo.findUserByAccountNumber(request.getDebitorAccountNumber());
            BigInteger debitUserBalance = debitUser.getAccountBalance().toBigInteger();
            BigInteger requestAmount = request.getAmount().toBigInteger();
            if (debitUserBalance.intValue()<requestAmount.intValue()) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountNumber(debitUser.getAccountNumber())
                                .accountBalance(debitUser.getAccountBalance())
                                .build())
                        .build();
            }


            User creditedUser = userRepo.findUserByAccountNumber(request.getBeneficiaryAccountNumber());
            creditedUser.setAccountBalance(creditedUser.getAccountBalance().add(request.getAmount()));
            userRepo.save(creditedUser);
            debitUser.setAccountBalance(debitUser.getAccountBalance().subtract(request.getAmount()));
            userRepo.save(debitUser);
            List<AccountInfo> accountInfoList = new ArrayList<>();
            accountInfoList.add(AccountInfo.builder()
                    .message("Debited party details")
                    .accountNumber(debitUser.getAccountNumber())
                    .accountName(debitUser.getFirstName() + " " + debitUser.getLastName() + " " + debitUser.getOtherName())
                    .accountBalance(debitUser.getAccountBalance())
                    .build());
            accountInfoList.add(AccountInfo.builder()
                    .message("Beneficiary party details")
                    .accountNumber(creditedUser.getAccountNumber())
                    .accountName(creditedUser.getFirstName() + " " + creditedUser.getLastName() + " " + creditedUser.getOtherName())
                    .accountBalance(creditedUser.getAccountBalance())
                    .build());

            //now send email alerts to both parties

            String sendersAccountNumber=request.getDebitorAccountNumber().toString().substring(0, request.getDebitorAccountNumber().toString().length() - 2).replaceAll("\\d", "X")
                    + "" + request.getDebitorAccountNumber().toString().substring(request.getDebitorAccountNumber().toString().length() - 3);
            EmailDetails emailDetailsCreditParty = EmailDetails.builder()
                    .recipient(creditedUser.getEmail())
                    .subject("Account XXXXXXX" + creditedUser.getAccountNumber().toString().substring(7) + " credited")
                    .messageBody(AccountUtils.ACCOUNT_CREDITED_EMAIL + " " + request.getAmount() +
                            " from " + sendersAccountNumber
                            + "\n" + "Remaining balance is : " + creditedUser.getAccountBalance()
                    )
                    .build();
            emailService.sendEmail(emailDetailsCreditParty);

            String recieverAccountNumber=request.getBeneficiaryAccountNumber().toString().substring(0, request.getBeneficiaryAccountNumber().toString().length() - 2).replaceAll("\\d", "X")
                    + "" + request.getBeneficiaryAccountNumber().toString().substring(request.getBeneficiaryAccountNumber().toString().length() - 3);

            EmailDetails emailDetailsDebitParty = EmailDetails.builder()
                    .recipient(debitUser.getEmail())
                    .subject("Account XXXXXXX" + debitUser.getAccountNumber().toString().substring(7) + " debited")
                    .messageBody(AccountUtils.ACCOUNT_DEBITED_EMAIL + " " + request.getAmount() + " to "
                            + recieverAccountNumber
                            + "\n" + "Remaining balance is : " + debitUser.getAccountBalance()
                    )
                    .build();
            emailService.sendEmail(emailDetailsDebitParty);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE + " " + AccountUtils.ACCOUNT_CREDITED_CODE)
                    .responseMessage("The account "+debitUser.getAccountNumber()+" "+AccountUtils.ACCOUNT_DEBITED_MESSAGE
                            + " The account " +creditedUser.getAccountNumber()+ " " + AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                    .accountInfoList(accountInfoList)
                    .build();
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users=userRepo.findAll();
        List<UserDTO> userDTOS=users.stream().map((user)->this.modelMapper.map(user,UserDTO.class)).toList();
        return userDTOS;
    }

    //GET ALL USERS/DISPLAY ALL USERS
    @Override
    public BankResponse getUserByAccountNumber(String accountNumber) {
        User user=userRepo.findUserByAccountNumber(accountNumber);
        boolean accountExists= userRepo.existsByAccountNumber(accountNumber);
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
                        .accountName(user.getFirstName()+" "+user.getLastName()+" "+user.getOtherName())
                        .accountBalance(user.getAccountBalance())
                        .message("Details of account are : ")
                        .build())
                .build();
    }




}
