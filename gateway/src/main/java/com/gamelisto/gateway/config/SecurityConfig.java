package com.gamelisto.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        // Deshabilitar CSRF (el gateway es stateless)
        .csrf(ServerHttpSecurity.CsrfSpec::disable)

        // Deshabilitar form login (usamos JWT)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

        // Deshabilitar HTTP Basic (usamos JWT)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

        // Permitir todas las peticiones (la autenticación se hace en el filtro)
        .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
        .build();
  }
}
