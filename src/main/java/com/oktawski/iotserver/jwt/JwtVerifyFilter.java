package com.oktawski.iotserver.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class JwtVerifyFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {


            String authorizationHeader = request.getHeader("Authorization");

            System.out.println(authorizationHeader);

            if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
                filterChain.doFilter(request, response);
                return;
            }

            try{
                String token = authorizationHeader.replace("Bearer ", "");
                String secret = "securesecuresecuresecuresecuresecure";
                //TODO export key to other class or app properties
                Jws<Claims> claimsJws = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                        .build().parseClaimsJws(token);

                Claims body = claimsJws.getBody();
                String username = body.getSubject();
                System.out.println("Username from JwtVerifyFilter " + username);
                List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");

                Set<SimpleGrantedAuthority> authoritySet = authorities.stream()
                        .map(v -> new SimpleGrantedAuthority(v.get("authority")))
                        .collect(Collectors.toSet());

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authoritySet);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
            catch(JwtException e){
                throw new IllegalStateException("Token cannot be trusted");
            }

            filterChain.doFilter(request, response);
    }
}
