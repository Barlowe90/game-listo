package com.gamelisto.usuarios.config;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.usuarios.domain.repositories.IEmailService;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * ConfiguraciÃ³n de test para messaging y servicios externos. Proporciona mocks de IUsuarioPublisher
 * e IEmailService para evitar dependencias externas en tests. Importa TestContainersConfig para
 * usar PostgreSQL en lugar de H2. La configuraciÃ³n de seguridad se maneja en TestSecurityConfig.
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



