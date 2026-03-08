package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.LogoutCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import com.gamelisto.usuarios.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios.domain.repositories.RepositorioJtiRevocados;
import com.gamelisto.usuarios.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios.shared.auth.JwtProperties;
import com.gamelisto.usuarios.shared.auth.JwtUtils;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutUseCase implements LogoutHandle {

  private final RepositorioRefreshTokens repositorioRefreshTokens;
  private final RepositorioJtiRevocados repositorioJtiRevocados;
  private final JwtProperties jwtProperties;

  @Transactional
  public void execute(LogoutCommand command) {
    // 1. Revocar refresh token
    TokenValue tokenValue = TokenValue.of(command.refreshToken());
    TokenHash tokenHash = TokenHash.from(tokenValue);
    RefreshToken refreshToken =
        repositorioRefreshTokens
            .buscarActivo(tokenHash)
            .orElseThrow(() -> new ApplicationException("Refresh token inválido"));

    repositorioRefreshTokens.revocar(tokenHash, refreshToken.getTtl());

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
        }
      } catch (Exception e) {
        // No lanzamos excepción, el refresh token ya fue revocado
      }
    }
  }
}
