package com.gamelisto.usuarios_service.config;

import static org.mockito.Mockito.mock;

import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuración de test para messaging. Proporciona un mock de IUsuarioPublisher para evitar
 * dependencias de RabbitMQ en tests.
 */
@TestConfiguration
public class TestMessagingConfig {

  @Bean
  @Primary
  public IUsuarioPublisher usuarioPublisher() {
    return mock(IUsuarioPublisher.class);
  }
}
