package com.sentineia.security;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class RevokedJwtRegistry {

    private final ConcurrentHashMap<String, Instant> revokedByJti = new ConcurrentHashMap<>();

    public void revoke(String jti, Instant expiresAt) {
        if (jti == null) {
            return;
        }
        evictExpired();
        revokedByJti.put(jti, expiresAt);
    }

    public boolean isRevoked(String jti) {
        if (jti == null) {
            return false;
        }
        evictExpired();
        Instant exp = revokedByJti.get(jti);
        if (exp == null) {
            return false;
        }
        if (exp.isBefore(Instant.now())) {
            revokedByJti.remove(jti);
            return false;
        }
        return true;
    }

    private void evictExpired() {
        Instant now = Instant.now();
        revokedByJti.entrySet().removeIf(e -> e.getValue().isBefore(now));
    }
}
