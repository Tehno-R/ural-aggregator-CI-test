package ural.ru.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ural.ru.security.filters.ExceptionFilterHandler;
import ural.ru.services.RoleService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/swagger-ui/**",
            "/v3/**",
            "/actuator/**",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/api/auth/logout/all",
    };

    private final ExceptionFilterHandler exceptionFilterHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(resourceServerConfigurer ->
                        resourceServerConfigurer.jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .authorizeHttpRequests(this::authorizeHttpRequests)
                .addFilterBefore(exceptionFilterHandler, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public Converter<Jwt, Collection<GrantedAuthority>> jwtAuthoritiesConverter(RoleService roleService) {
        return new AuthConverter(roleService);
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter(
            Converter<Jwt, Collection<GrantedAuthority>> jwtAuthoritiesConverter) {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length >= 2) {
                byte[] decodedHeader = Base64.getUrlDecoder().decode(tokenParts[0]);
                byte[] decodedPayload = Base64.getUrlDecoder().decode(tokenParts[1]);

                String decodedHeaderStr = new String(decodedHeader, StandardCharsets.UTF_8);
                String decodedPayloadStr = new String(decodedPayload, StandardCharsets.UTF_8);

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> headerMap;
                Map<String, Object> payloadMap;
                try {
                    headerMap = objectMapper.readValue(decodedHeaderStr, Map.class);
                    payloadMap = objectMapper.readValue(decodedPayloadStr, Map.class);
                } catch (JsonProcessingException e) {
                    log.error("Error when parsing jwt token {}", token, e);
                    throw new RuntimeException(e);
                }
                return new Jwt(token,
                        Instant.ofEpochMilli((Integer) payloadMap.get("iat")),
                        Instant.ofEpochMilli((Integer) payloadMap.get("exp")),
                        headerMap,
                        payloadMap);
            }
            log.error("Incorrect jwt token {}", token);
            throw new RuntimeException("Incorrect jwt token " + token);
        };
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
        return new JwtAuthenticationProvider(jwtDecoder);
    }

    private void authorizeHttpRequests(AbstractRequestMatcherRegistry<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl> auth) {
        auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest()
                .authenticated();
    }

}
