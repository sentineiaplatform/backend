package com.sentineia.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import com.sentineia.users.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final RevokedJwtRegistry revokedJwtRegistry;

    public JwtService(JwtProperties properties, RevokedJwtRegistry revokedJwtRegistry) {
        this.properties = properties;
        this.revokedJwtRegistry = revokedJwtRegistry;
    }

    public long getExpirationMs() {
        return properties.expirationMs();
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.expirationMs());
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("perfilId", user.getPerfil().getId().toString())
                .claim("perfilName", user.getPerfil().getName())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    public void revokeAccessToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        try {
            Claims c = parseAndValidate(rawToken.trim());
            String jti = c.getId();
            if (jti != null && c.getExpiration() != null) {
                revokedJwtRegistry.revoke(jti, c.getExpiration().toInstant());
            }
        } catch (Exception ignored) {
            // token inválido ou expirado: logout continua idempotente (204)
        }
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(String token) {
        String sub = parseAndValidate(token).getSubject();
        return UUID.fromString(sub);
    }

    private SecretKey signingKey() {
        String s = properties.secret();
        if (s.startsWith("base64:")) {
            byte[] raw = Decoders.BASE64.decode(s.substring("base64:".length()));
            return Keys.hmacShaKeyFor(raw);
        }
        return Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8));
    }
}
