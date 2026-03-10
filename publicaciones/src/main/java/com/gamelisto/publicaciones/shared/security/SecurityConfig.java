package com.gamelisto.publicaciones.shared.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para el microservicio de publicaciones.
 *
 * <p>Este servicio confía en la validación JWT realizada por el API Gateway. El Gateway envía
 * información del usuario autenticado en headers (X-User-Id, X-User-Roles, etc.), que son
 * procesados por GatewayAuthenticationFilter para construir el Authentication de Spring Security.
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
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health")
                    .permitAll()
                    // Permitir acceso público (sin autenticación) a GETs específicos de
                    // publicaciones
                    .requestMatchers(
                        HttpMethod.GET,
                        "/v1/publicaciones",
                        "/v1/publicaciones/",
                        "/v1/publicaciones/*",
                        "/v1/publicaciones/grupos/*",
                        "/v1/publicaciones/user/*",
                        "/v1/publicaciones/game/*")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    return http.build();
  }
}
