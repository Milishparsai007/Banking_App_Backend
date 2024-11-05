package org.example.bankingapplicationbackend.repository;

import org.example.bankingapplicationbackend.entity.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CredentialsRepo extends JpaRepository<Credentials,Long> {
  Credentials findByUsername(String username);
}
