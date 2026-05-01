package com.sentineia.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentineia.users.user.User;
import com.sentineia.users.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * Grava um evento por pedido HTTP relevante, sem depender de chamadas explícitas ao {@link AuditService}
 * em controladores ou serviços.
 */
@Component
public class AuditHttpLoggingInterceptor implements HandlerInterceptor {

    private final AuditService auditService;
    private final UserRepository userRepository;
    private final AuditHttpLoggingProperties properties;
    private final ObjectMapper objectMapper;

    public AuditHttpLoggingInterceptor(
            AuditService auditService,
            UserRepository userRepository,
            AuditHttpLoggingProperties properties,
            ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.userRepository = userRepository;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            @Nullable Exception ex) {
        if (!properties.isEnabled() || !shouldAudit(request)) {
            return;
        }
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        String action = buildAction(method, uri, status);
        String category = resolveCategory(uri);
        String detail = buildDetail(request, ex);

        Actor actor = resolveActor();
        auditService.record(actor.userId(), actor.email(), category, action, detail);
    }

    @Nullable
    private String buildDetail(HttpServletRequest request, @Nullable Exception ex) {
        List<String> parts = new ArrayList<>();
        String payload = extractPayloadForAudit(request);
        if (StringUtils.hasText(payload)) {
            parts.add("Corpo: " + payload);
        }
        if (ex != null) {
            parts.add(truncateException(ex));
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join("\n", parts);
    }

    @Nullable
    private String extractPayloadForAudit(HttpServletRequest request) {
        if (!properties.isIncludePayload()) {
            return null;
        }
        String uri = request.getRequestURI();
        for (String prefix : properties.getPayloadExcludedPathPrefixes()) {
            if (prefix != null && !prefix.isBlank() && uri.startsWith(prefix.trim())) {
                return null;
            }
        }
        if (HttpMethod.POST.matches(request.getMethod()) && "/api/users".equals(uri)) {
            return null;
        }
        String ct = request.getContentType();
        String lower = ct != null ? ct.toLowerCase(Locale.ROOT) : "";
        if (StringUtils.hasText(lower)) {
            if (lower.contains("multipart/") || lower.contains("application/octet-stream")) {
                return null;
            }
        }
        ContentCachingRequestWrapper wrapper = resolveContentCachingWrapper(request);
        if (wrapper == null) {
            return null;
        }
        byte[] buf = wrapper.getContentAsByteArray();
        if (buf == null || buf.length == 0) {
            return null;
        }
        int max = Math.max(256, properties.getPayloadMaxChars());
        boolean jsonByHeader = lower.contains("application/json") || lower.contains("+json");
        boolean jsonByBody = buf[0] == '{' || buf[0] == '[';
        if (jsonByHeader || jsonByBody) {
            return AuditPayloadRedactor.sanitizeJson(buf, objectMapper, max);
        }
        if (lower.startsWith("text/")) {
            return AuditPayloadRedactor.describeNonJsonBody(buf, max);
        }
        if (!StringUtils.hasText(lower)) {
            return AuditPayloadRedactor.sanitizeJson(buf, objectMapper, max);
        }
        return null;
    }

    /**
     * O pedido que chega ao interceptor pode estar envolvido por vários {@link HttpServletRequestWrapper}
     * (Spring Security, etc.); o {@link ContentCachingRequestWrapper} fica no interior.
     */
    @Nullable
    private static ContentCachingRequestWrapper resolveContentCachingWrapper(HttpServletRequest request) {
        HttpServletRequest current = request;
        for (int depth = 0; depth < 24 && current != null; depth++) {
            if (current instanceof ContentCachingRequestWrapper ccw) {
                return ccw;
            }
            if (current instanceof HttpServletRequestWrapper w) {
                current = (HttpServletRequest) w.getRequest();
            } else {
                break;
            }
        }
        return null;
    }

    private boolean shouldAudit(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return false;
        }
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/")) {
            return false;
        }
        for (String prefix : properties.getExcludedPathPrefixes()) {
            if (prefix != null && !prefix.isBlank() && uri.startsWith(prefix.trim())) {
                return false;
            }
        }
        String method = request.getMethod();
        boolean allowRead = properties.isIncludeGet()
                && (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method));
        boolean allowMutate = HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.PATCH.matches(method)
                || HttpMethod.DELETE.matches(method);
        if (!allowMutate && !allowRead) {
            return false;
        }
        return true;
    }

    private static String buildAction(String method, String uri, int status) {
        return method + " " + uri + " · " + status;
    }

    /**
     * Categorias alinhadas ao UI; desconhecidas caem em {@code geral} via frontend ou aqui.
     */
    private static String resolveCategory(String uri) {
        if (uri.startsWith("/api/auth/reset-password")) {
            return "seguranca";
        }
        if (uri.startsWith("/api/auth")) {
            return "auth";
        }
        if (uri.startsWith("/api/users/me/password")) {
            return "seguranca";
        }
        if (uri.startsWith("/api/users/me")) {
            return "perfil";
        }
        if (uri.startsWith("/api/users")) {
            return "membros";
        }
        if (uri.startsWith("/api/perfis")) {
            return "perfis";
        }
        if (uri.startsWith("/api/audit")) {
            return "geral";
        }
        if (uri.startsWith("/api/complaints")) {
            return "geral";
        }
        return "geral";
    }

    private Actor resolveActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Actor.EMPTY;
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof UserDetails ud)) {
            return Actor.EMPTY;
        }
        String email = ud.getUsername();
        if (email == null || email.isBlank()) {
            return Actor.EMPTY;
        }
        UUID id = userRepository.findByEmail(email.trim().toLowerCase()).map(User::getId).orElse(null);
        return new Actor(id, email.trim().toLowerCase());
    }

    private static String truncateException(Exception ex) {
        String name = ex.getClass().getSimpleName();
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            return name;
        }
        String combined = name + ": " + msg.trim();
        return combined.length() > 500 ? combined.substring(0, 499) + "…" : combined;
    }

    private record Actor(UUID userId, String email) {
        static final Actor EMPTY = new Actor(null, null);
    }
}
