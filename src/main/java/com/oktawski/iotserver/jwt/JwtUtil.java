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

/*    public User parseToken(String token){


        try{
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Constants.SECRET.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            User user = new User();
            user.setUsername(body.getSubject());
            user.setId(Long.parseLong((String)body.get("userId")));

            return user;
        }
        catch(JwtException | ClassCastException e){
            return null;
        }
    }*/

/*    public String generateToken(User user){
        Claims claims = Jwts.claims()
                .setSubject(user.getUsername());

        claims.put("userId", user.getId() + "");

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(Constants.SECRET.getBytes()))
                .compact();
    }*/

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
