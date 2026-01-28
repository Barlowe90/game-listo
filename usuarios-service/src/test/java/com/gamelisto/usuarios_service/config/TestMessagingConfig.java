package com.gamelisto.usuarios_service.config;

import static org.mockito.Mockito.mock;

import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuración de test para messaging y servicios externos. Proporciona mocks de
 * IUsuarioPublisher e IEmailService para evitar dependencias externas en tests.
 */
@TestConfiguration
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
}
