package com.gamelisto.biblioteca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestConfiguration
@EnableMethodSecurity
@Import(TestContainersConfig.class)
public class TestMessagingConfig {

  @Bean
  public MockMvc mockMvc(@Autowired WebApplicationContext webApplicationContext) {
    // Nota: evitamos aplicar `springSecurity()` aquí porque en el contexto de pruebas
    // actual esto fuerza la creación del springSecurityFilterChain y puede provocar
    // errores durante la inicialización del ApplicationContext si no se han
    // configurado todos los beans de seguridad necesarios para estas pruebas.
    // Para pruebas que requieran integración con Spring Security, crear una
    // configuración de test específica que exponga los beans necesarios.
    return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
}
