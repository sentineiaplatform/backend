package com.sentineia.email;

import com.sentineia.auth.passwordreset.PasswordResetProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.resend.core.exception.ResendException;

/**
 * E-mail de boas-vindas com palavra-passe inicial e links do frontend (login e alteração de senha).
 */
@Service
public class UserWelcomeEmailService {

    private static final Logger log = LoggerFactory.getLogger(UserWelcomeEmailService.class);

    private final PasswordResetProperties frontendProperties;
    private final ResendEmailService resendEmailService;

    public UserWelcomeEmailService(
            PasswordResetProperties frontendProperties,
            ObjectProvider<ResendEmailService> resendEmailProvider) {
        this.frontendProperties = frontendProperties;
        this.resendEmailService = resendEmailProvider.getIfAvailable();
    }

    /**
     * Envia credenciais iniciais. Se Resend não estiver ativo, regista aviso e não falha o pedido.
     */
    public void sendNewUserWelcome(String toEmail, String displayName, String plainPassword) {
        if (!StringUtils.hasText(toEmail) || !StringUtils.hasText(plainPassword)) {
            return;
        }
        if (resendEmailService == null) {
            log.warn(
                    "Novo utilizador {}: Resend inativo (sentineia.resend.api-key). E-mail de boas-vindas não enviado.",
                    toEmail);
            return;
        }
        String loginUrl = frontendProperties.frontendBaseUrl() + "/login";
        String securityUrl = frontendProperties.frontendBaseUrl() + "/app/configuracoes/seguranca";
        String name = StringUtils.hasText(displayName) ? displayName.trim() : "Utilizador";
        try {
            String html = buildEmailHtml(escapeHtml(name), escapeHtml(plainPassword), loginUrl, securityUrl);
            resendEmailService.sendHtml(toEmail.trim(), "Bem-vindo ao SentinelIA — dados de acesso", html);
        } catch (ResendException e) {
            log.error("Falha ao enviar e-mail de boas-vindas para {}", toEmail, e);
        } catch (IllegalStateException e) {
            log.error("Resend mal configurado (remetente?): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error(
                    "E-mail de boas-vindas não enviado para {}. Verifique sentineia.resend.from. Resposta: {}",
                    toEmail,
                    e.getMessage(),
                    e);
        }
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String buildEmailHtml(String safeName, String safePassword, String loginUrl, String securityUrl) {
        final String navy = "#1b2439";
        final String green = "#0f9488";
        final String muted = "#64748b";
        final String surface = "#f0f4f8";
        final String border = "#e2e8f0";

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Bem-vindo — SentinelIA</title>
                </head>
                <body style="margin:0;padding:0;background-color:%s;font-family:'Geist Variable',Geist,system-ui,-apple-system,sans-serif;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:%s;padding:32px 16px;">
                  <tr>
                    <td align="center">
                      <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:520px;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(27,36,57,0.08);border:1px solid %s;">
                        <tr>
                          <td style="background:linear-gradient(135deg,%s 0%%,#243047 100%%);padding:28px 32px 24px;">
                            <p style="margin:0 0 4px;font-size:11px;font-weight:600;letter-spacing:0.12em;text-transform:uppercase;color:rgba(255,255,255,0.55);">SentinelIA</p>
                            <h1 style="margin:0;font-size:22px;font-weight:700;line-height:1.25;color:#ffffff;letter-spacing:-0.02em;">Conta criada</h1>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:28px 32px 8px;">
                            <p style="margin:0 0 16px;font-size:15px;line-height:1.6;color:#1e293b;">Olá, %s</p>
                            <p style="margin:0 0 16px;font-size:15px;line-height:1.65;color:#334155;">A sua conta no SentinelIA foi registada com sucesso. Utilize a <strong style="color:#1b2439;font-weight:600;">palavra-passe inicial</strong> abaixo para entrar e altere-a logo que possível por segurança.</p>
                            <p style="margin:0 0 8px;font-size:13px;line-height:1.5;color:%s;font-weight:600;text-transform:uppercase;letter-spacing:0.06em;">Palavra-passe inicial</p>
                            <p style="margin:0 0 24px;padding:14px 16px;background:#f8fafc;border-radius:10px;border:1px solid %s;font-size:15px;font-family:ui-monospace,monospace;word-break:break-all;color:#1e293b;">%s</p>
                            <table role="presentation" cellspacing="0" cellpadding="0" style="margin:0 0 16px;">
                              <tr>
                                <td style="border-radius:10px;background:%s;">
                                  <a href="%s" style="display:inline-block;padding:14px 28px;font-size:15px;font-weight:600;color:#ffffff;text-decoration:none;border-radius:10px;background:%s;">Aceder ao sistema</a>
                                </td>
                              </tr>
                            </table>
                            <p style="margin:0 0 12px;font-size:14px;line-height:1.6;color:#334155;">Recomendamos <strong style="color:#1b2439;">alterar a palavra-passe</strong> após o primeiro acesso, em <em>Configurações → Segurança</em>:</p>
                            <p style="margin:0 0 24px;font-size:14px;line-height:1.55;"><a href="%s" style="color:%s;font-weight:600;">%s</a></p>
                            <p style="margin:0;font-size:13px;line-height:1.55;color:%s;">Se não esperava este e-mail, contacte o administrador da sua organização.</p>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:20px 32px 28px;border-top:1px solid %s;background:#fafbfc;">
                            <p style="margin:0 0 8px;font-size:12px;line-height:1.5;color:%s;">Link direto para o login (copiar se o botão não abrir):</p>
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
                        safeName,
                        muted,
                        border,
                        safePassword,
                        green,
                        loginUrl,
                        green,
                        securityUrl,
                        green,
                        securityUrl,
                        muted,
                        border,
                        muted,
                        muted,
                        loginUrl,
                        muted);
    }
}
