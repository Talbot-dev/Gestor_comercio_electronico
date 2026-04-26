package com.app.config.security.authProviders;

import com.app.config.security.configuration.SecurityAuditHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@Profile("keycloak")
@lombok.RequiredArgsConstructor
public class KeycloakSecurityConfiguration {

    private final SecurityAuditHandlers securityAuditHandlers;

    @Bean
    public SecurityFilterChain keycloakSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/public/**",
                                "/swagger-ui/**",
                                "/h2-console/**",
                                "/v3/api-docs/**",
                                "/*/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(securityAuditHandlers)
                        .accessDeniedHandler(securityAuditHandlers)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }

    static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess == null || !(realmAccess.get("roles") instanceof List<?> roles)) {
                return List.of();
            }

            List<GrantedAuthority> authorities = new ArrayList<>();
            for (Object role : roles) {
                if (role instanceof String nombreRol && !nombreRol.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + nombreRol.toUpperCase()));
                }
            }
            return authorities;
        }
    }
}
