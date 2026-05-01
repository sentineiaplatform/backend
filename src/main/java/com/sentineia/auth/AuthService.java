package com.sentineia.auth;

import com.sentineia.security.JwtService;
import com.sentineia.users.User;
import com.sentineia.users.UserRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenResponse login(String email, String rawPassword) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        String token = jwtService.generateAccessToken(user);
        return new TokenResponse(token, "Bearer", jwtService.getExpirationMs());
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return;
        }
        String raw = authorizationHeader.substring(7).trim();
        jwtService.revokeAccessToken(raw);
    }
}
