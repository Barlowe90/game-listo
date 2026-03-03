package com.gamelisto.usuarios.shared.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private String secret = "dev-secret-key-change-in-production-min-32-chars";

  private long expirationMs = 900000; // 5 minutos

  private long refreshExpirationMs = 604800000; // 7 dias
}
