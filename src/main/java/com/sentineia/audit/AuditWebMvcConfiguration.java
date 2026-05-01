package com.sentineia.audit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(AuditHttpLoggingProperties.class)
public class AuditWebMvcConfiguration implements WebMvcConfigurer {

    private final AuditHttpLoggingInterceptor auditHttpLoggingInterceptor;

    public AuditWebMvcConfiguration(AuditHttpLoggingInterceptor auditHttpLoggingInterceptor) {
        this.auditHttpLoggingInterceptor = auditHttpLoggingInterceptor;
    }

    /**
     * Executar cedo para envolver o pedido em {@link org.springframework.web.util.ContentCachingRequestWrapper},
     * permitindo ler o corpo na auditoria depois do handler.
     */
    @Bean
    public FilterRegistrationBean<AuditRequestContentCachingFilter> auditRequestContentCachingFilterRegistration() {
        FilterRegistrationBean<AuditRequestContentCachingFilter> reg =
                new FilterRegistrationBean<>(new AuditRequestContentCachingFilter());
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        reg.addUrlPatterns("/api/*");
        return reg;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditHttpLoggingInterceptor).addPathPatterns("/api/**");
    }
}
