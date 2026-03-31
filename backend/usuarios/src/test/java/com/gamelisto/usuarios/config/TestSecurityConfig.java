package com.gamelisto.usuarios.config;

import com.gamelisto.usuarios.shared.security.GatewayAuthenticationFilter;
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
 * ConfiguraciÃ³n de seguridad para el entorno de testing.
 *
 * <p>Incluye el mismo filtro {@link GatewayAuthenticationFilter} que en producciÃ³n para poder
 * simular headers del Gateway en los tests y validar @PreAuthorize.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("test")
public class TestSecurityConfig {

  @Bean
  public GatewayAuthenticationFilter gatewayAuthenticationFilter() {
    return new GatewayAuthenticationFilter();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.csrf(AbstractHttpConfigurer::disable)
        .addFilterBefore(gatewayAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        "/v1/usuarios/auth/register",
                        "/v1/usuarios/auth/verify-email",
                        "/v1/usuarios/auth/resend-verification",
                        "/v1/usuarios/auth/forgot-password",
                        "/v1/usuarios/auth/reset-password",
                        "/v1/usuarios/auth/login",
                        "/v1/usuarios/auth/refresh",
                        "/v1/usuarios/auth/logout")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    return http.build();
  }
}



