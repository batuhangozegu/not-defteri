package com.notdefteri.security;

import com.notdefteri.config.AppProperties;
import com.notdefteri.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/** JWT üretimi ve doğrulaması. İmza anahtarı ${JWT_SECRET} ortam değişkeninden gelir. */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(AppProperties props) {
        String secret = props.jwt().secret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET en az 32 byte (karakter) uzunluğunda olmalı. Örn: openssl rand -base64 32");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = props.jwt().expirationMinutes();
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(expirationMinutes))))
                .signWith(key)
                .compact();
    }

    /** Token geçerliyse kullanıcı id'sini döner, değilse boş Optional. */
    public UUID parseUserId(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return UUID.fromString(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
