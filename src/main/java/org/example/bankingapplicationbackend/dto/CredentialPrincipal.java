package org.example.bankingapplicationbackend.dto;

import lombok.*;
import org.example.bankingapplicationbackend.entity.Credentials;
import org.example.bankingapplicationbackend.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@NoArgsConstructor
@Getter
@Setter
@Builder
public class CredentialPrincipal implements UserDetails {

//    private Employee employee;
    private Credentials credentials;
    public CredentialPrincipal(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(credentials.getRole().toString()));
    }

    @Override
    public String getPassword() {
        return credentials.getPassword();
    }

    @Override
    public String getUsername() {
        return credentials.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
