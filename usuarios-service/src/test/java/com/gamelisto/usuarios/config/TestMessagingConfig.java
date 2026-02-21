package com.gamelisto.usuarios.config;

import static org.mockito.Mockito.mock;

import com.gamelisto.usuarios.domain.repositories.IEmailService;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de test para messaging y servicios externos. Proporciona mocks de IUsuarioPublisher
 * e IEmailService para evitar dependencias externas en tests. Importa TestContainersConfig para
 * usar PostgreSQL en lugar de H2. La configuración de seguridad se maneja en TestSecurityConfig.
 */
@TestConfiguration
@EnableMethodSecurity
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
}
