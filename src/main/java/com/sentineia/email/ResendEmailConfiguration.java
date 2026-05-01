package com.sentineia.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(ResendProperties.class)
public class ResendEmailConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailConfiguration.class);

    @Bean
    ApplicationRunner resendApiKeyPresenceLogger(Environment environment) {
        return args -> {
            if (!StringUtils.hasText(environment.getProperty("sentineia.resend.api-key"))) {
                log.warn(
                        "Resend desativado: defina sentinelia.resend.api-key (ex.: variável RESEND_API_KEY mapeada em produção). "
                                + "Nenhum bean Resend/ResendEmailService será criado.");
            }
        };
    }
}
