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

    private static List<String> defaultExcluded() {
        List<String> list = new ArrayList<>();
        list.add("/api/health");
        list.add("/api/test");
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
}
