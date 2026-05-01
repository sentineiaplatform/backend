package com.sentineia.users.user;

import java.util.UUID;

public record UserProfileUpdateResponse(
        UUID id,
        String name,
        String email,
        String accessToken,
        String tokenType,
        long expiresInMs) {
}
