package com.gamelisto.usuarios_service.infrastructure.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private String secret = "dev-secret-key-change-in-production-min-32-chars";

  /** 900000 ms = 15 minutos. */
  private long expirationMs = 900000;

  /** 604800000 ms = 7 días. */
  private long refreshExpirationMs = 604800000;

  // Getters y Setters

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public long getExpirationMs() {
    return expirationMs;
  }

  public void setExpirationMs(long expirationMs) {
    this.expirationMs = expirationMs;
  }

  public long getRefreshExpirationMs() {
    return refreshExpirationMs;
  }

  public void setRefreshExpirationMs(long refreshExpirationMs) {
    this.refreshExpirationMs = refreshExpirationMs;
  }
}
