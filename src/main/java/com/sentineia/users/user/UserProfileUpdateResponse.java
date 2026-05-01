package com.sentineia.users.user;

import java.util.UUID;

public record UserProfileUpdateResponse(
        UUID id,
        String name,
        String email,
        UUID perfilId,
        String perfilName,
        String accessToken,
        String tokenType,
        long expiresInMs) {
}
