package com.aqvp.platform.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${aqvp.security.jwt.secret:change-me-very-long-secret-key-at-least-256-bits}")
    private String jwtSecret;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<GrantedAuthority> extractAuthorities(String token) {
        final List<String> authorities = extractAllClaims(token).get("authorities", List.class);
        if (authorities == null) {
            return Collections.emptyList();
        }
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public boolean isTokenValid(String token) {
        try {
            return !extractClaim(token, Claims::getExpiration).before(new java.util.Date());
        } catch (Exception ex) {
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
