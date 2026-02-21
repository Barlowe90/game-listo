package com.gamelisto.api_gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración para JWT. Deben coincidir con las del usuarios-service para validar
 * tokens correctamente.
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private String secret;
  private Long expiration;
  private Long refreshExpiration;

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public Long getExpiration() {
    return expiration;
  }

  public void setExpiration(Long expiration) {
    this.expiration = expiration;
  }

  public Long getRefreshExpiration() {
    return refreshExpiration;
  }

  public void setRefreshExpiration(Long refreshExpiration) {
    this.refreshExpiration = refreshExpiration;
  }
}
