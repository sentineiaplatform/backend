package com.sentineia.audit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentineia.audit.http")
public class AuditHttpLoggingProperties {

    /** Quando falso, nenhum evento HTTP é gravado em audit_logs. */
    private boolean enabled = true;

    /** Incluir pedidos GET (além de POST, PUT, PATCH, DELETE). */
    private boolean includeGet = false;

    /** Prefixos de caminho a ignorar (ex.: /api/health). */
    private List<String> excludedPathPrefixes = defaultExcluded();

    /**
     * Quando verdadeiro, anexa ao detalhe da auditoria um excerto do corpo JSON (campos sensíveis mascarados).
     * Desligado por defeito — ativar só em ambientes controlados (RGPD / segredos).
     */
    private boolean includePayload = false;

    /** Tamanho máximo do excerto do corpo gravado no campo {@code detail}. */
    private int payloadMaxChars = 2000;

    /** Nunca registar corpo nestes prefixos (login, passwords, tokens). */
    private List<String> payloadExcludedPathPrefixes = defaultPayloadExcluded();

    private static List<String> defaultExcluded() {
        List<String> list = new ArrayList<>();
        list.add("/api/health");
        list.add("/api/test");
        return list;
    }

    private static List<String> defaultPayloadExcluded() {
        List<String> list = new ArrayList<>();
        list.add("/api/auth/login");
        list.add("/api/auth/reset-password");
        list.add("/api/auth/forgot-password");
        list.add("/api/users/me/password");
        return list;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isIncludeGet() {
        return includeGet;
    }

    public void setIncludeGet(boolean includeGet) {
        this.includeGet = includeGet;
    }

    public List<String> getExcludedPathPrefixes() {
        return excludedPathPrefixes;
    }

    public void setExcludedPathPrefixes(List<String> excludedPathPrefixes) {
        this.excludedPathPrefixes = excludedPathPrefixes;
    }

    public boolean isIncludePayload() {
        return includePayload;
    }

    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    public int getPayloadMaxChars() {
        return payloadMaxChars;
    }

    public void setPayloadMaxChars(int payloadMaxChars) {
        this.payloadMaxChars = payloadMaxChars;
    }

    public List<String> getPayloadExcludedPathPrefixes() {
        return payloadExcludedPathPrefixes;
    }

    public void setPayloadExcludedPathPrefixes(List<String> payloadExcludedPathPrefixes) {
        this.payloadExcludedPathPrefixes = payloadExcludedPathPrefixes;
    }
}
