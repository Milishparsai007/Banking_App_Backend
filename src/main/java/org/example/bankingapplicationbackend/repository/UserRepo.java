package org.example.bankingapplicationbackend.repository;

import org.example.bankingapplicationbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

//it will help manipulate the database table of type User with primary key of type Long
public interface UserRepo extends JpaRepository<User,Long> {
    Boolean existsByEmail(String email);
    Boolean existsByAccountNumber(String accountNumber); //these are predefined methods to check in the database.
    User findUserByAccountNumber(String accountNumber);
}
