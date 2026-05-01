package com.sentineia.auth;

public record TokenResponse(String accessToken, String tokenType, long expiresInMs) {
}
