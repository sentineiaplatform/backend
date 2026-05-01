package com.sentineia.security;

import java.io.IOException;
import java.util.UUID;

import com.sentineia.users.User;

import io.jsonwebtoken.Claims;
import com.sentineia.users.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RevokedJwtRegistry revokedJwtRegistry;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            RevokedJwtRegistry revokedJwtRegistry,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.revokedJwtRegistry = revokedJwtRegistry;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Claims claims = jwtService.parseAndValidate(token);
            String jti = claims.getId();
            if (revokedJwtRegistry.isRevoked(jti)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            UUID userId = UUID.fromString(claims.getSubject());
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            UserDetails principal = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities("ROLE_USER")
                    .build();
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
