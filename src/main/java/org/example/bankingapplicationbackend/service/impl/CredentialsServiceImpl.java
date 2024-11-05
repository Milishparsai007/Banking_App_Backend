package org.example.bankingapplicationbackend.service.impl;

import jakarta.transaction.Transactional;
import org.example.bankingapplicationbackend.dto.CredentialsDto;
import org.example.bankingapplicationbackend.dto.LoginRequest;
import org.example.bankingapplicationbackend.entity.Credentials;
import org.example.bankingapplicationbackend.repository.CredentialsRepo;
import org.example.bankingapplicationbackend.service.impl.securityService.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CredentialsServiceImpl {
    @Autowired
    private CredentialsRepo credentialsRepo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    public void addUser(CredentialsDto credentialsDto)
    {
        Credentials credentials=Credentials.builder()
                .username(credentialsDto.getUserName())
                .password(credentialsDto.getPassword())
                .role(credentialsDto.getRole())
                .build();
        credentialsRepo.save(credentials);
    }

    public String verify(LoginRequest loginRequest) {
        Authentication authentication=
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),loginRequest.getPassword()
                ));

        if(authentication.isAuthenticated())
        {
            return jwtService.generateToken(loginRequest.getUserName());
        }
        return "Fail";
    }
}
