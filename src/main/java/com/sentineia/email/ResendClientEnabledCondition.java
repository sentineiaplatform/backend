package com.sentineia.email;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Ativa o cliente Resend apenas quando existe chave de API não vazia.
 */
public final class ResendClientEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String key = context.getEnvironment().getProperty("sentineia.resend.api-key");
        return StringUtils.hasText(key);
    }
}
