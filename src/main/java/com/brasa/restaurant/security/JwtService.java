package com.brasa.restaurant.security;

import com.brasa.restaurant.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey key;
    private final long minutes;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.expiration-minutes}") long minutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.minutes = minutes;
    }

    public String generate(Usuario u) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(u.getEmail())
            .claim("uid", u.getId())
            .claim("rid", u.getRestauranteId())
            .claim("role", u.getPerfil().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(minutes, ChronoUnit.MINUTES)))
            .signWith(key)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}