package com.gamelisto.social.config;

import com.gamelisto.social.infrastructure.in.security.GatewayAuthenticationFilter;
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
 * Configuración de seguridad para el entorno de testing en el módulo social.
 *
 * <p>Incluye el mismo filtro GatewayAuthenticationFilter que en producción para poder simular
 * headers del Gateway en los tests y validar @PreAuthorize.
 */
@Configuration
@Profile("test")
@EnableWebSecurity
@EnableMethodSecurity
public class TestSecurityConfig {

  @Bean
  public GatewayAuthenticationFilter gatewayAuthenticationFilter() {
    return new GatewayAuthenticationFilter();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        // Agregar filtro que procesa headers del Gateway (simulados en tests)
        .addFilterBefore(gatewayAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        // Permitir todas las peticiones - la autorización se maneja con @PreAuthorize
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

    return http.build();
  }
}
