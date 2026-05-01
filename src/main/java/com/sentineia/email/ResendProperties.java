package com.sentineia.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentineia.resend")
public record ResendProperties(String apiKey, String from) {

    public ResendProperties {
        apiKey = apiKey == null ? "" : apiKey.trim();
        from = from == null ? "" : from.trim();
    }
}
