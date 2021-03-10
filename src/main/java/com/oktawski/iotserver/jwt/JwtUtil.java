package com.oktawski.iotserver.jwt;

import com.oktawski.iotserver.security.SecurityConstants;
import com.oktawski.iotserver.user.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;

@Component
public class JwtUtil {

    public String getUsername(String token){
        var secret = SecurityConstants.SECRET;

        var stripedToken = token.replace("Bearer ", "");

        var claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(stripedToken)
                .getBody();

        var username = claims.getSubject();

        return username;
    }

    public static String createToken(Authentication authentication){
        var secret = SecurityConstants.SECRET;

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authentication.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
}