package com.sentineia.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.resend.Resend;

@Configuration
@Conditional(ResendClientEnabledCondition.class)
class ResendEmailBeansConfiguration {

    @Bean
    Resend resend(ResendProperties properties) {
        return new Resend(properties.apiKey());
    }

    @Bean
    ResendEmailService resendEmailService(Resend resend, ResendProperties properties) {
        return new ResendEmailService(resend, properties);
    }
}
