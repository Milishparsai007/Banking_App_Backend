package org.example.bankingapplicationbackend.securityConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bankingapplicationbackend.service.impl.securityService.JwtService;
import org.example.bankingapplicationbackend.service.impl.securityService.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    ApplicationContext context;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");

        String token=null;
        String username=null;

        if(authHeader!=null && authHeader.startsWith("Bearer "))
        {
            token=authHeader.substring(7);
            username=jwtService.extractUserName(token);
        }

        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
        {
            //it will give you an object of class userdetails to validate the token
            //we want the object of UserDetails to have the user data
            UserDetails userDetails=context.getBean(MyUserDetailService.class).loadUserByUsername(username);
            if(jwtService.validateToken(token,userDetails))
            {
                UsernamePasswordAuthenticationToken token1=new UsernamePasswordAuthenticationToken(userDetails
                        ,null,userDetails.getAuthorities());
                token1.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token1);
            }

        }
        filterChain.doFilter(request,response);
    }
}
