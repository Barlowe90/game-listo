package com.gamelist.catalogo.config;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.gamelist.catalogo.domain.repositories.IGamePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
  public IGamePublisher eventPublisher() {
    return mock(IGamePublisher.class);
  }

  @Bean
  public MockMvc mockMvc(@Autowired WebApplicationContext webApplicationContext) {
    return MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
