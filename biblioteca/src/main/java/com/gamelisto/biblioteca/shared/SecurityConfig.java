package com.gamelisto.biblioteca.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para catalogo.
 *
 * <p>Este microservicio confía en el API Gateway (Spring Cloud Gateway) para la validación de
 * tokens JWT. Las peticiones llegan ya autenticadas con headers X-User-* enriquecidos por el
 * gateway.
 *
 * <p>Rutas protegidas por rol: - Sync endpoints (/v1/catalogo/sync/**) → solo ADMIN (validado via
 * X-User-Role header) - Read endpoints (/v1/catalogo/games/**, /v1/catalogo/platforms/**) →
 * públicas (cualquier usuario autenticado)
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
        // Agregar filtro que procesa headers del Gateway
        .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        // Permitir todas las peticiones - la autorización se maneja con @PreAuthorize
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

    return http.build();
  }
}
