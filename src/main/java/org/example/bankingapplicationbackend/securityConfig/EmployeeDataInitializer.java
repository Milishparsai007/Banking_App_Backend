package org.example.bankingapplicationbackend.securityConfig;

import org.example.bankingapplicationbackend.entity.Credentials;
import org.example.bankingapplicationbackend.entity.Employee;
import org.example.bankingapplicationbackend.entity.Role;
import org.example.bankingapplicationbackend.repository.CredentialsRepo;
import org.example.bankingapplicationbackend.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
public class EmployeeDataInitializer {

    @Autowired
    private EmployeeRepo employeeRepository;
    @Autowired
    private CredentialsRepo credentialsRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Bean
    public ApplicationRunner initializeEmployeeData() {
        return args -> {
            if (employeeRepository.findByUserName("dummyEmployee") == null) {
                Employee employee = new Employee();
                employee.setUserName("dummyEmployee");
                employee.setPassword(passwordEncoder.encode("dummyPassword"));
                employeeRepository.save(employee);
                System.out.println("Dummy employee created for authentication.");
            }
        };
    }
    @Bean
    public ApplicationRunner initializeCredentialData() {
        return args -> {
            if (credentialsRepo.findByUsername("dummyEmployee") == null) {
                Credentials credentials = new Credentials();
                credentials.setUsername("dummyEmployee");
                credentials.setPassword(passwordEncoder.encode("dummyPassword"));
                credentials.setRole(Role.EMPLOYEE.toString());
                credentialsRepo.save(credentials);
                System.out.println("Dummy credentials created for authentication.");
            }
        };
    }
}
