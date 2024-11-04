package org.example.bankingapplicationbackend.repository;

import org.example.bankingapplicationbackend.entity.Customers;
import org.example.bankingapplicationbackend.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transactions,String> {
    Boolean existsByAccountNum(String accountNum);
    Transactions findTransactionsByTransactionId(String transcationId);
    List<Transactions> findByCustomers(Customers customers);
    List<Transactions> findByCreatedAtBetweenOrderByCreatedAt(LocalDate startDate, LocalDate endDate);
    @Query("SELECT t.remainingBalance FROM Transactions t WHERE t.transactionId = :transactionId")
    BigDecimal findRemainingBalanceByTransactionId(@Param("transactionId") String transactionId);

    @Query("SELECT t FROM Transactions t WHERE t.customers = :customers AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt ASC")
    List<Transactions> findTransactionsByUserAndDateRange(
            @Param("customers") Customers customers,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(c.accountBalance, 0) + " +
            "COALESCE(SUM(CASE WHEN t.transactionType = 'CREDIT' THEN t.amount END), 0) - " +
            "COALESCE(SUM(CASE WHEN t.transactionType = 'DEBIT' THEN t.amount END), 0) " +
            "FROM Customers c JOIN c.transactionsList t " +
            "WHERE c = :customers AND t.createdAt <= :date")
    BigDecimal findAccountBalanceByUserAndDate(@Param("customers") Customers customers, @Param("date") LocalDate date);

}
