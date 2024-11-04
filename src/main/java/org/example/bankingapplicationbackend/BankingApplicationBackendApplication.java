package org.example.bankingapplicationbackend;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.example.bankingapplicationbackend.entity.Employee;
import org.example.bankingapplicationbackend.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Application",
                description = "Backend REST APIs for the application",
                version = "v1.0",
                contact = @Contact(
                        name = "Milish Parsai",
                        email = "milishparsaispringboot@gmail.com",
                        url = "https://github.com/Milishparsai007/Banking_App_Backend"
                        ),
                license = @License(
                        name = "Bank App",
                        url = "https://github.com/Milishparsai007/Banking_App_Backend"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Read Documentation for getting insights to the application",
                url="https://github.com/Milishparsai007/Banking_App_Backend"
        )
)
public class BankingApplicationBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplicationBackendApplication.class, args);
    }

}
