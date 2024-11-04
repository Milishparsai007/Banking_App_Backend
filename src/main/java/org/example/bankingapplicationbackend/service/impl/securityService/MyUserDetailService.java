package org.example.bankingapplicationbackend.service.impl.securityService;

import org.example.bankingapplicationbackend.dto.EmployeePrincipal;
import org.example.bankingapplicationbackend.entity.Employee;
import org.example.bankingapplicationbackend.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    EmployeeRepo employeeRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee=employeeRepo.findByUserName(username);
        if(employee==null)
        {
            System.out.println("Username not found");
            throw new UsernameNotFoundException("Employee not found");
        }
        EmployeePrincipal principal=new EmployeePrincipal(employee);
        return principal;
    }
}
