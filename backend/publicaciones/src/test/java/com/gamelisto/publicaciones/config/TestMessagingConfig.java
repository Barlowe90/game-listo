package com.gamelisto.publicaciones.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@Import(TestContainersConfig.class)
public class TestMessagingConfig {

  @Bean
  public MockMvc mockMvc(@Autowired WebApplicationContext webApplicationContext) {
    return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
}
