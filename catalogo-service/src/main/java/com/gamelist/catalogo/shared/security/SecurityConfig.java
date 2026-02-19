package com.gamelist.catalogo.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para catalogo-service.
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
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth
                    // Swagger / OpenAPI - públicas
                    .requestMatchers(
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**")
                    .permitAll()
                    // Actuator health - pública
                    .requestMatchers("/actuator/health", "/actuator/info")
                    .permitAll()
                    // Endpoints de lectura del catálogo - públicos
                    .requestMatchers(
                        "/v1/catalogo/health", "/v1/catalogo/games/**", "/v1/catalogo/platforms/**")
                    .permitAll()
                    // Endpoints de sincronización - permiso total (el gateway controla acceso)
                    // La autorización real se delega al API Gateway que valida X-User-Role
                    .anyRequest()
                    .permitAll());

    return http.build();
  }
}
