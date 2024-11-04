package org.example.bankingapplicationbackend.repository;

import org.example.bankingapplicationbackend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee,String> {
    Employee findByUserName(String username);
}
