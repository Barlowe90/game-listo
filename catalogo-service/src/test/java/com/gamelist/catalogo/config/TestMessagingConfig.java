package com.gamelist.catalogo.config;

import static org.mockito.Mockito.mock;

import com.gamelist.catalogo.domain.repositories.IEventPublisherPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuración de test para messaging y servicios externos.
 *
 * <p>Proporciona mocks de servicios externos para evitar dependencias en tests: -
 * IEventPublisherPort: Mock para RabbitMQ event publishing
 *
 * <p>Esta configuración debe importarse en tests de integración
 * con @Import(TestMessagingConfig.class)
 */
@TestConfiguration
public class TestMessagingConfig {

  /**
   * Mock de IEventPublisherPort para evitar dependencia de RabbitMQ en tests. Reemplaza el bean
   * EventPublisherRabbitMQ en contexto de test.
   */
  @Bean
  @Primary
  public IEventPublisherPort eventPublisher() {
    return mock(IEventPublisherPort.class);
  }
}
