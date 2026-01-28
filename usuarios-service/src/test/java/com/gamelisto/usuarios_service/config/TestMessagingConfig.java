package com.gamelisto.usuarios_service.config;

import static org.mockito.Mockito.mock;

import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de test para messaging, servicios externos, seguridad y base de datos. Proporciona
 * mocks de IUsuarioPublisher e IEmailService para evitar dependencias externas en tests. Importa
 * TestContainersConfig para usar PostgreSQL en lugar de H2.
 */
@TestConfiguration
@EnableWebSecurity
@Import(TestContainersConfig.class)
public class TestMessagingConfig {

  @Bean
  @Primary
  public IUsuarioPublisher usuarioPublisher() {
    return mock(IUsuarioPublisher.class);
  }

  @Bean
  @Primary
  public IEmailService emailService() {
    return mock(IEmailService.class);
  }

  @Bean
  @Primary
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** Configuración de seguridad para tests que permite todas las peticiones sin autenticación. */
  @Bean
  @Primary
  public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }
}
