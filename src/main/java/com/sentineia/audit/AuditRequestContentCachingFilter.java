package com.sentineia.audit;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * Permite ler o corpo do pedido mais tarde (ex.: auditoria), porque o stream só pode ser consumido uma vez.
 */
public class AuditRequestContentCachingFilter extends OncePerRequestFilter {

    /** Limite de buffer para não carregar uploads enormes na memória (corpo truncado pelo wrapper). */
    private static final int CONTENT_CACHE_LIMIT = 48 * 1024;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request instanceof ContentCachingRequestWrapper) {
            filterChain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT);
        filterChain.doFilter(wrapped, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith("/api/");
    }
}
