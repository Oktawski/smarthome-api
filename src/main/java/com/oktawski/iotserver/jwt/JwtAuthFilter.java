package com.oktawski.iotserver.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authManager;

    public JwtAuthFilter(AuthenticationManager authManager){
        this.authManager = authManager;
        setFilterProcessesUrl("/user/signin");
    }

    //TODO make request readable multiple times
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        try {
            UsernameAndPasswordAuthRequest authRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernameAndPasswordAuthRequest.class);
	    
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
            );

            return authManager.authenticate(authentication);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        // temp
        String username = authResult.getName();
        System.out.println("AuthFilter username: " + username);
        // temp

        String token = JwtUtil.createToken(authResult);

        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("access-control-expose-headers", "Authorization");

        response.getWriter().write(
                "{\"" + "BearerToken" + "\":\"" + token + "\"}"
        );
    }
}
