package com.sentineia.auth.passwordreset;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

import com.sentineia.email.ResendEmailService;
import com.sentineia.users.user.User;
import com.sentineia.users.user.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.resend.core.exception.ResendException;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetProperties properties;
    private final ResendEmailService resendEmailService;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordResetProperties properties,
            org.springframework.beans.factory.ObjectProvider<ResendEmailService> resendEmailProvider) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.properties = properties;
        this.resendEmailService = resendEmailProvider.getIfAvailable();
    }

    /**
     * Gera token e envia e-mail se o utilizador existir. Resposta ao cliente deve ser sempre genérica
     * (não revelar se o e-mail existe). Se Resend não estiver configurado, regista aviso e não envia.
     */
    @Transactional
    public void requestReset(String rawEmail) {
        if (!StringUtils.hasText(rawEmail)) {
            return;
        }
        String email = rawEmail.trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }
        User user = userOpt.get();

        tokenRepository.deleteByUser(user);

        String token = generateOpaqueToken();
        Instant expiresAt = Instant.now().plus(properties.tokenValidityMinutes(), ChronoUnit.MINUTES);

        PasswordResetToken entity = new PasswordResetToken();
        entity.setUser(user);
        entity.setToken(token);
        entity.setExpiresAt(expiresAt);
        entity.setUsed(false);
        tokenRepository.save(entity);

        String link = buildResetLink(token);
        if (resendEmailService == null) {
            log.warn(
                    "Recuperação de senha: token criado para {} mas Resend não está ativo (sentineia.resend.api-key). E-mail não enviado.",
                    email);
            return;
        }

        try {
            String html = buildEmailHtml(link, properties.tokenValidityMinutes());
            resendEmailService.sendHtml(
                    user.getEmail(),
                    "Recuperação de senha — SentinelIA",
                    html);
        } catch (ResendException e) {
            log.error("Falha ao enviar e-mail de recuperação para {}", email, e);
        } catch (IllegalStateException e) {
            log.error("Resend mal configurado (remetente?): {}", e.getMessage());
        }
    }

    private static String generateOpaqueToken() {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String buildResetLink(String token) {
        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
        return properties.frontendBaseUrl() + properties.resetPathNormalized() + "?token=" + encoded;
    }

    private static String buildEmailHtml(String resetLink, int validityMinutes) {
        return """
                <!DOCTYPE html>
                <html lang="pt">
                <body style="font-family: system-ui, sans-serif; line-height: 1.5; color: #111;">
                <p>Olá,</p>
                <p>Recebeu este e-mail porque foi pedida a <strong>recuperação da senha</strong> na SentinelIA.</p>
                <p><a href="%s" style="color: #0d9488;">Redefinir senha</a></p>
                <p style="font-size: 14px; color: #555;">Este link expira em aproximadamente <strong>%d</strong> minutos.</p>
                <p style="font-size: 14px; color: #555;">Se não pediu esta alteração, pode ignorar este e-mail.</p>
                </body>
                </html>
                """
                .formatted(resetLink, validityMinutes);
    }
}
