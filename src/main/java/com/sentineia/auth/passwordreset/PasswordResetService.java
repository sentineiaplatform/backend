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
import com.sentineia.users.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.resend.core.exception.ResendException;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetProperties properties;
    private final ResendEmailService resendEmailService;
    private final UserService userService;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordResetProperties properties,
            UserService userService,
            org.springframework.beans.factory.ObjectProvider<ResendEmailService> resendEmailProvider) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.properties = properties;
        this.userService = userService;
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
        } catch (RuntimeException e) {
            // SDK Resend pode lançar RuntimeException (ex. 403 domínio `from` não verificado).
            log.error(
                    "E-mail de recuperação não enviado para {}. Verifique `sentineia.resend.from`: o domínio do remetente tem de estar verificado em https://resend.com/domains (em testes use `onboarding@resend.dev`). Resposta: {}",
                    email,
                    e.getMessage(),
                    e);
        }
    }

    /**
     * Define nova palavra-passe com token válido e não utilizado. Marca o token como usado.
     */
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        if (!StringUtils.hasText(rawToken)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token é obrigatório.");
        }
        String token = rawToken.trim();
        PasswordResetToken prt = tokenRepository
                .findByTokenAndUsedIsFalse(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Link inválido ou já utilizado. Peça um novo e-mail de recuperação."));

        if (prt.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Este link expirou. Solicite uma nova recuperação de senha.");
        }

        userService.updatePasswordFromReset(prt.getUser(), newPassword);
        prt.setUsed(true);
        tokenRepository.save(prt);
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

    /**
     * HTML alinhado à marca (navy + verde da app, Geist/system-ui). Layout em tabela para clientes de e-mail.
     */
    private static String buildEmailHtml(String resetLink, int validityMinutes) {
        // Cores ≈ :root em frontend (index.css): brand-navy oklch(0.22…264), brand-green oklch(0.56…163)
        final String navy = "#1b2439";
        final String green = "#0f9488";
        final String muted = "#64748b";
        final String surface = "#f0f4f8";
        final String border = "#e2e8f0";

        return """
                <!DOCTYPE html>
                <html lang="pt">
                <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Recuperação de senha — SentinelIA</title>
                </head>
                <body style="margin:0;padding:0;background-color:%s;font-family:'Geist Variable',Geist,system-ui,-apple-system,sans-serif;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:%s;padding:32px 16px;">
                  <tr>
                    <td align="center">
                      <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:520px;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(27,36,57,0.08);border:1px solid %s;">
                        <tr>
                          <td style="background:linear-gradient(135deg,%s 0%%,#243047 100%%);padding:28px 32px 24px;">
                            <p style="margin:0 0 4px;font-size:11px;font-weight:600;letter-spacing:0.12em;text-transform:uppercase;color:rgba(255,255,255,0.55);">SentinelIA</p>
                            <h1 style="margin:0;font-size:22px;font-weight:700;line-height:1.25;color:#ffffff;letter-spacing:-0.02em;">Recuperação de senha</h1>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:28px 32px 8px;">
                            <p style="margin:0 0 16px;font-size:15px;line-height:1.6;color:#1e293b;">Olá,</p>
                            <p style="margin:0 0 24px;font-size:15px;line-height:1.65;color:#334155;">Recebeu este e-mail porque foi pedida a <strong style="color:#1b2439;font-weight:600;">recuperação da palavra-passe</strong> na sua conta SentinelIA.</p>
                            <table role="presentation" cellspacing="0" cellpadding="0" style="margin:0 0 24px;">
                              <tr>
                                <td style="border-radius:10px;background:%s;">
                                  <a href="%s" style="display:inline-block;padding:14px 28px;font-size:15px;font-weight:600;color:#ffffff;text-decoration:none;border-radius:10px;background:%s;">Redefinir palavra-passe</a>
                                </td>
                              </tr>
                            </table>
                            <p style="margin:0 0 12px;font-size:13px;line-height:1.55;color:%s;">Este link deixa de ser válido em aproximadamente <strong style="color:#1b2439;">%d minutos</strong>.</p>
                            <p style="margin:0;font-size:13px;line-height:1.55;color:%s;">Se não foi você, pode ignorar este e-mail — a sua palavra-passe não será alterada.</p>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:20px 32px 28px;border-top:1px solid %s;background:#fafbfc;">
                            <p style="margin:0 0 8px;font-size:12px;line-height:1.5;color:%s;">O botão não funciona? Copie e cole este endereço no navegador:</p>
                            <p style="margin:0;word-break:break-all;font-size:11px;line-height:1.45;color:%s;">%s</p>
                          </td>
                        </tr>
                      </table>
                      <p style="margin:20px 0 0;font-size:11px;color:%s;text-align:center;">© SentinelIA · comunicação automática</p>
                    </td>
                  </tr>
                </table>
                </body>
                </html>
                """
                .formatted(
                        surface,
                        surface,
                        border,
                        navy,
                        green,
                        resetLink,
                        green,
                        muted,
                        validityMinutes,
                        muted,
                        border,
                        muted,
                        muted,
                        resetLink,
                        muted);
    }
}
