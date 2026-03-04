package com.gamelisto.publicaciones.infrastructure.in.messaging;

import com.gamelisto.publicaciones.application.usecases.EntradaEventosHandle;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestMocksConfig {

  @Bean
  @Primary
  public EntradaEventosHandle entradaEventosHandle() {
    return Mockito.mock(EntradaEventosHandle.class);
  }
}
