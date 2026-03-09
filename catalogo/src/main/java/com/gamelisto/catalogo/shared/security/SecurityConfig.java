package com.gamelisto.catalogo.shared.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para catalogo.
 *
 * <p>Este microservicio confía en el API Gateway (Spring Cloud Gateway) para la validación de
 * tokens JWT. Las peticiones llegan ya autenticadas con headers X-User-* enriquecidos por el
 * gateway.
 *
 * <p>Esto permite usar @PreAuthorize en los controladores para control de acceso basado en roles.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

  private final GatewayAuthenticationFilter gatewayAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.csrf(AbstractHttpConfigurer::disable)
        .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }
}
