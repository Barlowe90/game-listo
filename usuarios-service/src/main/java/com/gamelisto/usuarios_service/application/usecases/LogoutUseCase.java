package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.LogoutCommand;
import com.gamelisto.usuarios_service.domain.exceptions.RefreshTokenInvalidoException;
import com.gamelisto.usuarios_service.domain.refreshtoken.Jti;
import com.gamelisto.usuarios_service.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios_service.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios_service.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioJtiRevocados;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios_service.infrastructure.auth.JwtProperties;
import com.gamelisto.usuarios_service.infrastructure.auth.JwtUtils;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutUseCase {

  private static final Logger logger = LoggerFactory.getLogger(LogoutUseCase.class);

  private final RepositorioRefreshTokens repositorioRefreshTokens;
  private final RepositorioJtiRevocados repositorioJtiRevocados;
  private final JwtProperties jwtProperties;

  public LogoutUseCase(
      RepositorioRefreshTokens repositorioRefreshTokens,
      RepositorioJtiRevocados repositorioJtiRevocados,
      JwtProperties jwtProperties) {
    this.repositorioRefreshTokens = repositorioRefreshTokens;
    this.repositorioJtiRevocados = repositorioJtiRevocados;
    this.jwtProperties = jwtProperties;
  }

  @Transactional
  public void execute(LogoutCommand command) {
    logger.debug("Procesando logout");

    // 1. Revocar refresh token
    TokenValue tokenValue = TokenValue.of(command.refreshToken());
    TokenHash tokenHash = TokenHash.from(tokenValue);
    RefreshToken refreshToken =
        repositorioRefreshTokens
            .buscarActivo(tokenHash)
            .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token inválido"));

    repositorioRefreshTokens.revocar(tokenHash, refreshToken.getTtl());
    logger.debug("Refresh token revocado");

    // 2. Si se proporciona access token, revocar su JTI (blacklist)
    if (command.accessToken() != null && !command.accessToken().isBlank()) {
      try {
        Claims claims = JwtUtils.parseToken(command.accessToken(), jwtProperties.getSecret());
        String jtiString = claims.get("jti", String.class);
        Jti jti = Jti.of(jtiString);

        // Calcular TTL residual del access token
        Date expiration = claims.getExpiration();
        Duration ttl = Duration.between(Instant.now(), expiration.toInstant());

        if (ttl.isPositive()) {
          repositorioJtiRevocados.revocar(jti, ttl);
          logger.debug("JTI del access token revocado (blacklist)");
        }
      } catch (Exception e) {
        logger.warn("No se pudo revocar JTI del access token: {}", e.getMessage());
        // No lanzamos excepción, el refresh token ya fue revocado
      }
    }

    logger.info("Logout completado");
  }
}
