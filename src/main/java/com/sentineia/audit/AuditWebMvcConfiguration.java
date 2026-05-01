package com.sentineia.audit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(AuditHttpLoggingProperties.class)
public class AuditWebMvcConfiguration implements WebMvcConfigurer {

    private final AuditHttpLoggingInterceptor auditHttpLoggingInterceptor;

    public AuditWebMvcConfiguration(AuditHttpLoggingInterceptor auditHttpLoggingInterceptor) {
        this.auditHttpLoggingInterceptor = auditHttpLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditHttpLoggingInterceptor).addPathPatterns("/api/**");
    }
}
