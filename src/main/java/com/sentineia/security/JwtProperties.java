package com.sentineia.security;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentineia.jwt")
public record JwtProperties(String secret, long expirationMs) {

    public JwtProperties {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "sentineia.jwt.secret deve ter pelo menos 32 bytes (ex.: string longa ou Base64).");
        }
        if (expirationMs <= 0) {
            throw new IllegalArgumentException("sentineia.jwt.expiration-ms deve ser > 0.");
        }
    }
}
