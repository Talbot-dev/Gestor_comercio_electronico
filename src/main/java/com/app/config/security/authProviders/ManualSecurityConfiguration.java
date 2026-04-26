package com.app.config.security.authProviders;

import com.app.config.security.configuration.SecurityAuditHandlers;
import com.app.usuario.model.Usuario;
import com.app.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@Profile("manual")
@RequiredArgsConstructor
public class ManualSecurityConfiguration {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityAuditHandlers securityAuditHandlers;

    @Bean
    public SecurityFilterChain manualSecurityFilterChain(HttpSecurity http) throws Exception {
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
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(securityAuditHandlers)
                        .accessDeniedHandler(securityAuditHandlers)
                )
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario usuario = usuarioService.buscarPorNombre(username);
            return User.builder()
                    .username(usuario.getNombre())
                    .password(usuario.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())))
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
