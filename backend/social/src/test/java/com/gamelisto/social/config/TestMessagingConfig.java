package com.gamelisto.social.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * ConfiguraciÃ³n de test para messaging y MockMvc en el mÃ³dulo social. Provee un bean MockMvc
 * construido desde el WebApplicationContext.
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



