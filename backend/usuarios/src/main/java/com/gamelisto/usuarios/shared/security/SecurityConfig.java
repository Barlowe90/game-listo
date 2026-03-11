package com.gamelisto.usuarios.shared.security;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

/**
 * Configuración de seguridad para el microservicio de usuarios.
 *
 * <p>Este servicio confía en la validación JWT realizada por el API Gateway. El Gateway envía
 * información del usuario autenticado en headers (X-User-Id, X-User-Roles, etc.), que son
 * procesados por {@link GatewayAuthenticationFilter} para construir el Authentication de Spring
 * Security.
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
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {

    // Solo GET /v1/usuarios/{uuid} será público (exacto)
    RegexRequestMatcher publicUserById =
        new RegexRequestMatcher("^/v1/usuarios/[0-9a-fA-F\\-]{36}$", "GET");

    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers("/actuator/health")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        "/v1/usuarios/auth/register",
                        "/v1/usuarios/auth/verify-email",
                        "/v1/usuarios/auth/resend-verification",
                        "/v1/usuarios/auth/forgot-password",
                        "/v1/usuarios/auth/reset-password",
                        "/v1/usuarios/auth/login",
                        "/v1/usuarios/auth/refresh")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/usuarios/auth/me")
                    .authenticated()
                    .requestMatchers(publicUserById)
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/usuarios/auth/logout")
                    .authenticated()
                    .requestMatchers("/v1/usuarios/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated());

    return http.build();
  }
}
