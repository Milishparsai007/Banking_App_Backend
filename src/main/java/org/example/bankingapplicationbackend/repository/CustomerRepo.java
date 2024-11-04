package org.example.bankingapplicationbackend.repository;

import org.example.bankingapplicationbackend.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

//it will help manipulate the database table of type Customers with primary key of type Long
public interface CustomerRepo extends JpaRepository<Customers,Long> {
    Boolean existsByEmail(String email);
    Boolean existsByAccountNumber(String accountNumber); //these are predefined methods to check in the database.
    Customers findCustomersByAccountNumber(String accountNumber);
}
