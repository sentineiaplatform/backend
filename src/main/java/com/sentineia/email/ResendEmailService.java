package com.sentineia.email;

import org.springframework.util.StringUtils;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

/**
 * Envio de e-mail HTML via Resend. Disponível apenas quando {@code sentinelia.resend.api-key} está definida.
 */
public class ResendEmailService {

    private final Resend resend;
    private final ResendProperties properties;

    public ResendEmailService(Resend resend, ResendProperties properties) {
        this.resend = resend;
        this.properties = properties;
    }

    /**
     * Envia e-mail HTML usando o remetente configurado em {@code sentinelia.resend.from}.
     *
     * @return id da mensagem na Resend
     * @throws IllegalStateException se o remetente global não estiver configurado
     * @throws IllegalArgumentException se destinatário, assunto ou corpo forem inválidos
     */
    public String sendHtml(String to, String subject, String html) throws ResendException {
        if (!StringUtils.hasText(to)) {
            throw new IllegalArgumentException("Destinatário (to) é obrigatório.");
        }
        if (!StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("Assunto é obrigatório.");
        }
        if (!StringUtils.hasText(html)) {
            throw new IllegalArgumentException("Corpo HTML é obrigatório.");
        }
        if (!StringUtils.hasText(properties.from())) {
            throw new IllegalStateException(
                    "Remetente não configurado: defina sentinelia.resend.from (endereço verificado na Resend).");
        }

        CreateEmailOptions request = CreateEmailOptions.builder()
                .from(properties.from())
                .to(to.trim())
                .subject(subject.trim())
                .html(html)
                .build();

        CreateEmailResponse response = resend.emails().send(request);
        return response.getId();
    }
}
