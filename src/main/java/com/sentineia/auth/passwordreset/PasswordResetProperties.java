package com.sentineia.auth.passwordreset;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentineia.password-reset")
public record PasswordResetProperties(String frontendBaseUrl, Integer tokenValidityMinutes, String resetPath) {

    public PasswordResetProperties {
        String base =
                frontendBaseUrl == null || frontendBaseUrl.isBlank()
                        ? "http://localhost:5173"
                        : frontendBaseUrl.trim().replaceAll("/+$", "");
        int minutes = tokenValidityMinutes == null || tokenValidityMinutes <= 0 ? 60 : tokenValidityMinutes;
        String path = resetPath == null || resetPath.isBlank() ? "/redefinir-senha" : resetPath.trim();
        frontendBaseUrl = base;
        tokenValidityMinutes = minutes;
        resetPath = path;
    }

    public String resetPathNormalized() {
        return resetPath.startsWith("/") ? resetPath : "/" + resetPath;
    }
}
