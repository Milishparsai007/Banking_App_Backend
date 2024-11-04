package org.example.bankingapplicationbackend.service.impl.bankStatementService;


import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankingapplicationbackend.dto.EmailDetails;
import org.example.bankingapplicationbackend.dto.TransactionDTO;
import org.example.bankingapplicationbackend.entity.Customers;
import org.example.bankingapplicationbackend.entity.Transactions;
import org.example.bankingapplicationbackend.repository.TransactionRepo;
import org.example.bankingapplicationbackend.repository.CustomerRepo;
import org.example.bankingapplicationbackend.service.impl.emailService.EmailService;
import org.example.bankingapplicationbackend.utils.AccountUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.util.List;

import static com.itextpdf.text.PageSize.*;


@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class BankStatement {

    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private CustomerRepo customerRepo;
    private ModelMapper modelMapper=new ModelMapper();
    @Autowired
    private EmailService emailService;
    private static final String FILE="D:\\SpringBoot_Bank_Backend_Statements\\MyStatement.pdf";


    public List<TransactionDTO> generateBankStatement(String accountNumber,String startDate,String endDate)
    {
        LocalDate start=LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end=LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        Customers customers = customerRepo.findCustomersByAccountNumber(accountNumber);
//        List<Transactions> transactionsByUser=transactionRepo.findByUser(customers);
        List<Transactions> transactionsByUser=transactionRepo.findTransactionsByUserAndDateRange(customers,start,end);
        List<TransactionDTO> list=transactionsByUser.stream().map((transaction)->this.modelMapper.map(transaction, TransactionDTO.class)).toList();
        return list;
    }

    public List<TransactionDTO> sendBankStatementToEmail(String accountNumber, String startDate, String endDate)
            throws FileNotFoundException, DocumentException {

        Customers customers = customerRepo.findCustomersByAccountNumber(accountNumber);
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        List<Transactions> transactionsList = transactionRepo.findTransactionsByUserAndDateRange(customers, start, end);
        List<TransactionDTO> transactionDTOList = transactionsList.stream()
                .map(transaction -> this.modelMapper.map(transaction, TransactionDTO.class)).toList();

        // DESIGNING THE PDF
        Rectangle statementSize = new Rectangle(A4);
        Document document = new Document(statementSize);
        log.info("Setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Capture the current date for "Report Date"
        LocalDate currentDate = LocalDate.now();
        String reportCreatedAt = currentDate.format(DateTimeFormatter.ISO_DATE);

        // Get the opening balance for the customers as of the startDate
        BigDecimal openingBalance = transactionRepo.findAccountBalanceByUserAndDate(customers, start);

        // Center-align and increase font size for the bank name table
        PdfPTable nameOfTheBank = new PdfPTable(1);
        PdfPCell name = new PdfPCell(new Phrase("MP BANK"));
        name.setHorizontalAlignment(Element.ALIGN_CENTER);  // Center-align the text
        name.setBorder(0);  // No border
        name.getPhrase().getFont().setSize(17f);  // Increase font size by 5 units (12 + 5 = 17)
        nameOfTheBank.addCell(name);

        PdfPCell spaceWithoutBorder = new PdfPCell();
        PdfPCell space = new PdfPCell();
        spaceWithoutBorder.setBorder(0);  // No border
        nameOfTheBank.addCell(spaceWithoutBorder);

        // Add table nameofthebank and some space between the name and other tables
        document.add(nameOfTheBank);
        document.add(new Paragraph(" "));  // Empty paragraph for space

        // Bank details table (with extra columns)
        PdfPTable bankInfoTable = new PdfPTable(3);  // Increase columns from 2 to 3
        bankInfoTable.setWidths(new float[]{6f, 1f, 5f});  // Adjust widths with space column

        Font smallerFont = new Font(Font.FontFamily.HELVETICA, 10f);  // Reduce font size by 2 units (12 - 2 = 10)

        PdfPCell bankName = new PdfPCell(new Phrase("MP Bank", smallerFont));
        bankName.setBorder(0);
        PdfPCell bankAddress = new PdfPCell(new Phrase("Address: B3, Patang Plaza Phase-5, Trimurti Chowk, Pune", smallerFont));
        bankAddress.setBorder(0);
        PdfPCell ifsc = new PdfPCell(new Phrase("IFSC Code: " + AccountUtils.IFSC_Code, smallerFont));
        ifsc.setBorder(0);
        PdfPCell bankContact = new PdfPCell(new Phrase("Branch mail: " + AccountUtils.BANK_MAIL, smallerFont));
        bankContact.setBorder(0);

        String userName = customers.getFirstName() + " " + customers.getOtherName() + " " + customers.getLastName();
        PdfPCell customerName = new PdfPCell(new Phrase("Account Name: " + userName, smallerFont));
        customerName.setBorder(0);
        PdfPCell customerAccNum = new PdfPCell(new Phrase("Account Number: " + customers.getAccountNumber(), smallerFont));
        customerAccNum.setBorder(0);
        PdfPCell dateOfStatement = new PdfPCell(new Phrase("Report Date: " + reportCreatedAt, smallerFont));
        dateOfStatement.setBorder(0);
        PdfPCell customerAddress = new PdfPCell(new Phrase("Address: " + customers.getAddress(), smallerFont));
        customerAddress.setBorder(0);
        PdfPCell customerContactNum = new PdfPCell(new Phrase("Contact Number: " + customers.getPhoneNumber(), smallerFont));
        customerContactNum.setBorder(0);

        // Add bank and customer details to the table
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(spaceWithoutBorder);  // Inserted empty column
        bankInfoTable.addCell(customerName);
        bankInfoTable.addCell(bankAddress);
        bankInfoTable.addCell(spaceWithoutBorder);  // Inserted empty column
        bankInfoTable.addCell(customerAccNum);

        bankInfoTable.addCell(ifsc);
        bankInfoTable.addCell(spaceWithoutBorder);  // Inserted empty column
        bankInfoTable.addCell(dateOfStatement);
        bankInfoTable.addCell(bankContact);
        bankInfoTable.addCell(spaceWithoutBorder);  // Inserted empty column
        bankInfoTable.addCell(customerAddress);
        bankInfoTable.addCell(spaceWithoutBorder);  // Inserted empty column
        bankInfoTable.addCell(spaceWithoutBorder);  // Inserted empty column
        bankInfoTable.addCell(customerContactNum);

        // Add bankinfortable and some space before the next table
        document.add(bankInfoTable);
        document.add(new Paragraph(" "));  // Empty paragraph for space

        // Statement period paragraph
        String para = "STATEMENT OF ACCOUNT FOR THE PERIOD FROM " + startDate + " TO " + endDate;
        Paragraph paragraph = new Paragraph(para, smallerFont);

        // Message table
        PdfPTable message = new PdfPTable(1);
        PdfPCell messageDisplay = new PdfPCell(new Phrase(paragraph));
        messageDisplay.setBorder(0);
        message.addCell(messageDisplay);
        message.addCell(spaceWithoutBorder);

        // Add message table and some space before the transactions table
        document.add(message);
        document.add(new Paragraph(" "));  // Empty paragraph for space

        // Transactions table with bold header and column widths adjusted
        PdfPTable transactionsTable = new PdfPTable(5);
        transactionsTable.setWidths(new float[]{2.5f, 4f, 4f, 2.5f, 2.5f});  // Adjust column widths for content

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD);  // Bold and increase font size by 2 units

        PdfPCell date = new PdfPCell(new Phrase("DATE", headerFont));
        PdfPCell particulars = new PdfPCell(new Phrase("PARTICULARS", headerFont));
        PdfPCell withdrawals = new PdfPCell(new Phrase("WITHDRAWALS", headerFont));
        PdfPCell deposits = new PdfPCell(new Phrase("DEPOSITS", headerFont));
        PdfPCell balance = new PdfPCell(new Phrase("BALANCE", headerFont));
        transactionsTable.addCell(date);
        transactionsTable.addCell(particulars);
        transactionsTable.addCell(withdrawals);
        transactionsTable.addCell(deposits);
        transactionsTable.addCell(balance);

        // Insert the row for Opening Balance
        PdfPCell openingBalanceCell = new PdfPCell(new Phrase("Opening Balance as of " + startDate, smallerFont));
        // Merge the cells from the first column to the deposits column (3 columns)
        openingBalanceCell.setColspan(4); // Merges date, particulars, and withdrawals cells
        openingBalanceCell.setHorizontalAlignment(Element.ALIGN_LEFT); // Align left
        transactionsTable.addCell(openingBalanceCell);
        // Create empty cells for withdrawals and deposits in the same row
//        transactionsTable.addCell(spaceWithoutBorder);  // Empty cell for withdrawals
//        transactionsTable.addCell(spaceWithoutBorder);  // Empty cell for deposits
        transactionsTable.addCell(new Phrase(openingBalance.toString(), smallerFont)); // Add opening balance to the last cell


//        transactionsTable.addCell(space);
//        transactionsTable.addCell(openingParticularsCell);
//        transactionsTable.addCell(emptyWithdrawalsCell);
//        transactionsTable.addCell(emptyDepositsCell);
//        transactionsTable.addCell(openingBalanceCell);

        // Add transaction data to the table
        transactionDTOList.forEach(transactionDTO -> {
            transactionsTable.addCell(new Phrase(transactionDTO.getCreatedAt(), smallerFont));
            transactionsTable.addCell(new Phrase(transactionDTO.getDescription(), smallerFont));

            if (transactionDTO.getTransactionType().equals("DEBIT")) {
                transactionsTable.addCell(new Phrase(transactionDTO.getAmount().toString(), smallerFont));
                transactionsTable.addCell(space);  // No deposit for debit transaction
            } else {
                transactionsTable.addCell(space);  // No withdrawal for credit transaction
                transactionsTable.addCell(new Phrase(transactionDTO.getAmount().toString(), smallerFont));
            }

            String remainingBalance = transactionRepo.findRemainingBalanceByTransactionId(transactionDTO.getTransactionId()).toString();
            transactionsTable.addCell(new Phrase(remainingBalance, smallerFont));
        });

        // Add transactions table to the document
        document.add(transactionsTable);
        Paragraph signature=new Paragraph("This is a system generated report hence does not require any signature.");
        document.close();

        //NOW SENDING EMAIL TO THE USER.
        EmailDetails bankStatement=EmailDetails.builder()
                .recipient(customers.getEmail())
                .subject("Statement of your Account "+ customers.getAccountNumber().substring(0,7)
                        .replaceAll("\\d","X")+""+ customers.getAccountNumber().substring(7))
                .messageBody("Please find the attached account statement for the Account "+ customers.getAccountNumber().substring(0,7)
                        .replaceAll("\\d","X")+""+ customers.getAccountNumber().substring(7)
                        +" from requested period between "+start+" to "+end)
                .attachment(FILE)
                .build();
        emailService.sendEmailWithAttachment(bankStatement);

        return transactionDTOList;
    }


}
