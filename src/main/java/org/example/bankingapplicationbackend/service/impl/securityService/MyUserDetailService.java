package org.example.bankingapplicationbackend.service.impl.securityService;

import org.example.bankingapplicationbackend.dto.CredentialPrincipal;
import org.example.bankingapplicationbackend.entity.Credentials;
import org.example.bankingapplicationbackend.repository.CredentialsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    CredentialsRepo credentialsRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Employee employee=employeeRepo.findByUserName(username);
        Credentials credentials=credentialsRepo.findByUsername(username);
        if(credentials==null)
        {
            System.out.println("Username not found");
            throw new UsernameNotFoundException("Employee not found");
        }
        CredentialPrincipal principal=new CredentialPrincipal(credentials);
        return principal;
    }
}
