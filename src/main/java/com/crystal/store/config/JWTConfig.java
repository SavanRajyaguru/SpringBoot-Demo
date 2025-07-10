package com.crystal.store.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JWTConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Bean
    public String generateToken(Object data) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        System.out.println(key);
        String jwtToken = Jwts.builder()
                .subject(data.toString())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
        return jwtToken;
    }

    // @Bean
    // public String validateToken(String token) {
    // Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    // return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    // }

}
