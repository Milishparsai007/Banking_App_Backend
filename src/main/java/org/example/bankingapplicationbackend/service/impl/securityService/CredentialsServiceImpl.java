package org.example.bankingapplicationbackend.service.impl.securityService;

import jakarta.transaction.Transactional;
import org.example.bankingapplicationbackend.dto.CredentialsDto;
import org.example.bankingapplicationbackend.dto.JwtAuthResponse;
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

    public JwtAuthResponse verify(LoginRequest loginRequest) {
        Authentication authentication=
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),loginRequest.getPassword()
                ));
        var credential =credentialsRepo.findByUsername(loginRequest.getUserName());
        var jwt=jwtService.generateToken(credential);

        JwtAuthResponse jwtAuthResponse=new JwtAuthResponse();
        jwtAuthResponse.setToken(jwt);

        if(authentication.isAuthenticated())
        {
            return new JwtAuthResponse(jwtService.generateToken(credential));
        }
        return new JwtAuthResponse("FAIL");
    }
}
