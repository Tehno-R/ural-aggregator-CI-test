package ural.ru.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ural.ru.converters.AuthConverter;
import ural.ru.converters.JwtConverter;
import ural.ru.filters.ExceptionFilterHandler;
import ural.ru.services.RoleSecurityService;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Configuration
public class AuthConfig {

    @Bean
    @Order(LOWEST_PRECEDENCE)
    @ConditionalOnMissingBean
    public AllowedUrls allowedUrls() {
        var allowedUrls = new String[]{
                "/swagger-ui/**",
                "/v3/**",
                "/actuator/**",
                "/api/auth/login",
                "/api/auth/refresh",
                "/api/auth/logout",
                "/api/auth/logout/all"
        };

        return () -> allowedUrls;
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter(
            AuthConverter authConverter
    ) {
        return new JwtConverter(authConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthConverter authConverter(RoleSecurityService roleSecurityService) {
        return new AuthConverter(roleSecurityService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleSecurityService roleSecurityService() {
        return new RoleSecurityService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionFilterHandler exceptionFilterHandler(
            @Qualifier("handlerExceptionResolver")
            HandlerExceptionResolver resolver
    ) {
        return new ExceptionFilterHandler(resolver);
    }

}
