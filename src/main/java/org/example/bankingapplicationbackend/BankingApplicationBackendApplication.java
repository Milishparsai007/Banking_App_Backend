package org.example.bankingapplicationbackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Application",
                description = "Backend REST APIs for the application",
                version = "v1.0",
                contact = @Contact(
                        name = "Milish Parsai",
                        email = "milishparsaispringboot@gmail.com",
                        url =
                        )
        )
)
public class BankingApplicationBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplicationBackendApplication.class, args);
    }

}
