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
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health")
                    .permitAll()
                    // aqui hay 2 opciones, o hago un permitAll() y añado security en endpoints
                    // o protejo ahora excepto el health y no se me olvida añadir nada
                    .anyRequest()
                    .authenticated());
    return http.build();
  }
}
