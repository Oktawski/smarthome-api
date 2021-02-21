package com.oktawski.iotserver.jwt;

import com.oktawski.iotserver.user.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String getUsername(String token){
        String secret = "securesecuresecuresecuresecuresecure";
        String tok = token.replace("Bearer ", "");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor("securesecuresecuresecuresecuresecure".getBytes()))
                .build()
                .parseClaimsJws(tok)
                .getBody();

        String username = claims.getSubject();

        return username;
    }
}
