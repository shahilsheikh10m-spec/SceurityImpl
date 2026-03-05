package com.shahil.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String secretKey;


    public JwtService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256); // 256-bit key
            SecretKey sk = keyGen.generateKey();
            this.secretKey = Base64.getEncoder()
                    .encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate key", e);
        }
    }

    public String generateToken(String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 30)
                )
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🔹 Extract username from token
    public String extractUsername(String tokenName) {
        return extractClaim(tokenName, Claims::getSubject);
    }

    // 🔹 Extract expiration
    public Date extractExpiration(String tokenName) {
        return extractClaim(tokenName, Claims::getExpiration);
    }

    // 🔹 Generic method to extract claims
    public <T> T extractClaim(String tokenName, Function<Claims, T> claimsResolver) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(tokenName)
                .getBody();

        return claimsResolver.apply(claims);
    }

    // 🔹 Check if token expired
    private boolean isTokenExpired(String tokenName) {
        return extractExpiration(tokenName).before(new Date());
    }

    // 🔹 Validate token
    public boolean validateToken(String tokenName, UserDetails userDetails) {

        final String username = extractUsername(tokenName);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(tokenName);
    }
}