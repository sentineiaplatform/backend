package com.sentineia.audit;

import java.util.UUID;

import com.sentineia.users.user.User;
import com.sentineia.users.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Grava um evento por pedido HTTP relevante, sem depender de chamadas explícitas ao {@link AuditService}
 * em controladores ou serviços.
 */
@Component
public class AuditHttpLoggingInterceptor implements HandlerInterceptor {

    private final AuditService auditService;
    private final UserRepository userRepository;
    private final AuditHttpLoggingProperties properties;

    public AuditHttpLoggingInterceptor(
            AuditService auditService,
            UserRepository userRepository,
            AuditHttpLoggingProperties properties) {
        this.auditService = auditService;
        this.userRepository = userRepository;
        this.properties = properties;
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
        String detail = ex != null ? truncateException(ex) : null;

        Actor actor = resolveActor();
        auditService.record(actor.userId(), actor.email(), category, action, detail);
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
