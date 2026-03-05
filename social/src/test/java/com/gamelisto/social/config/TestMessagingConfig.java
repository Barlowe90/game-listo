package com.gamelisto.social.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Configuración de test para messaging y MockMvc en el módulo social. Provee un bean MockMvc
 * construido desde el WebApplicationContext.
 */
@TestConfiguration
@EnableMethodSecurity
@Import({})
public class TestMessagingConfig {

  @Bean
  public MockMvc mockMvc(@Autowired WebApplicationContext webApplicationContext) {
    // No aplicamos springSecurity() aquí para evitar forzar la creación del filter chain
    return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
