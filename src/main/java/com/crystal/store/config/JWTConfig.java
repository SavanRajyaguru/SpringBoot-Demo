package com.crystal.store.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JWTConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(Map<String, String> data) {
        Key key = getSigningKey();
        System.out.println("Key: " + key);
        String jwtToken = Jwts.builder()
                .setSubject(data.toString())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 24 * 60 * 60 * 1000))
                .signWith(key)
                .compact();
        return jwtToken;
    }

    public String extractDataFromToken(String token) {
        Key key = getSigningKey();
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return claims.getBody().getSubject();
    }

    public boolean validateToken(String token) {
        Key key = getSigningKey();
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Key getSigningKey() {
        // Use Keys.hmacShaKeyFor with a key that's at least 256 bits (32 bytes)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // If the key is too short, use a secure key generation method
        if (keyBytes.length < 32) {
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
