package com.gamelisto.catalogo.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/** Configuración de WebClient para comunicación HTTP con IGDB API. */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final IgdbProperties igdbProperties;

  /**
   * WebClient configurado para llamadas a IGDB API.
   *
   * <p>Headers por defecto: - Content-Type: text/plain (IGDB command) - Client-ID: ID de aplicación
   * de IGDB - Accept: application/json
   *
   * @return WebClient configurado para IGDB
   */
  @Bean
  public WebClient igdbWebClient() {
    ExchangeStrategies strategies =
        ExchangeStrategies.builder()
            .codecs(
                configurer ->
                    configurer
                        .defaultCodecs()
                        .maxInMemorySize(igdbProperties.getMaxResponseBufferSize()))
            .build();

    return WebClient.builder()
        .baseUrl(igdbProperties.getBaseUrl())
        .exchangeStrategies(strategies)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("Client-ID", igdbProperties.getClientId())
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + igdbProperties.getAccessToken())
        .build();
  }
}
