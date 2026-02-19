package com.gamelist.catalogo.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/** Configuración de WebClient para comunicación HTTP con IGDB API. */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final IgdbProperties igdbProperties;

  /**
   * WebClient configurado para llamadas a IGDB API.
   *
   * <p>Headers por defecto: - Content-Type: application/json - Client-ID: ID de aplicación de IGDB
   *
   * @return WebClient configurado para IGDB
   */
  @Bean
  public WebClient igdbWebClient() {
    return WebClient.builder()
        .baseUrl(igdbProperties.getBaseUrl())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("Client-ID", igdbProperties.getClientId())
        .build();
  }
}
