package com.gamelisto.catalogo.shared.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Propiedades de configuración para la API de IGDB.
 *
 * <p>Estas propiedades se cargan desde application.properties con el prefijo "igdb".
 */
@Configuration
@ConfigurationProperties(prefix = "igdb")
@Validated
@Data
public class IgdbProperties {

  /** Client ID de la aplicación registrada en IGDB */
  @NotBlank(message = "IGDB Client ID es requerido")
  private String clientId;

  /** Access Token de IGDB (obtenido manualmente desde Twitch OAuth2) */
  @NotBlank(message = "IGDB Access Token es requerido")
  private String accessToken;

  /** URL base de la API de IGDB */
  @NotBlank(message = "IGDB Base URL es requerida")
  private String baseUrl = "https://api.igdb.com/v4";

  /** Timeout para las peticiones HTTP en milisegundos */
  private int timeout = 30000; // 30 segundos

  /** Tamaño máximo de batch para sincronización */
  private int batchSize = 100;

  /** Tamaño máximo en bytes que WebClient debe almacenar en memoria al leer la respuesta */
  private int maxResponseBufferSize = 16 * 1024 * 1024; // 16 MB por defecto
}
