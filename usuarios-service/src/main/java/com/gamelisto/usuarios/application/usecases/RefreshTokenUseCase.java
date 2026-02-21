package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.AuthResponseDTO;
import com.gamelisto.usuarios.application.dto.RefreshTokenCommand;
import com.gamelisto.usuarios.application.dto.TokenDTO;
import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import com.gamelisto.usuarios.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.shared.auth.JwtProperties;
import com.gamelisto.usuarios.shared.auth.JwtUtils;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenUseCase {

  private final RepositorioUsuarios repositorioUsuarios;
  private final RepositorioRefreshTokens repositorioRefreshTokens;
  private final JwtProperties jwtProperties;

  public RefreshTokenUseCase(
      RepositorioUsuarios repositorioUsuarios,
      RepositorioRefreshTokens repositorioRefreshTokens,
      JwtProperties jwtProperties) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.repositorioRefreshTokens = repositorioRefreshTokens;
    this.jwtProperties = jwtProperties;
  }

  @Transactional
  public AuthResponseDTO execute(RefreshTokenCommand command) {

    TokenValue tokenValue = TokenValue.of(command.refreshToken());
    TokenHash tokenHash = TokenHash.from(tokenValue);
    RefreshToken refreshToken =
        repositorioRefreshTokens
            .buscarActivo(tokenHash)
            .orElseThrow(() -> new ApplicationException("Refresh token inválido o revocado"));

    if (repositorioRefreshTokens.estaRevocado(tokenHash)) {
      throw new ApplicationException("Refresh token revocado");
    }

    if (refreshToken.isExpired()) {
      repositorioRefreshTokens.revocar(tokenHash, Duration.ofSeconds(60));
      throw new ApplicationException("Refresh token expirado");
    }

    Usuario usuario =
        repositorioUsuarios
            .findById(refreshToken.getUsuarioId())
            .orElseThrow(() -> new ApplicationException("Usuario no encontrado"));

    // ✅ Refresh Token Rotation: revocamos el token antiguo inmediatamente
    repositorioRefreshTokens.revocar(tokenHash, refreshToken.getTtl());

    Jti jti = Jti.generate();
    String accessTokenString =
        JwtUtils.generateAccessToken(
            usuario, jti, jwtProperties.getSecret(), jwtProperties.getExpirationMs());
    Instant accessTokenExpiresAt = Instant.now().plusMillis(jwtProperties.getExpirationMs());

    TokenValue newRefreshTokenValue = TokenValue.generate();
    String newRefreshTokenString = newRefreshTokenValue.value();
    TokenHash newRefreshTokenHash = TokenHash.from(newRefreshTokenValue);
    Instant newRefreshTokenExpiresAt =
        Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs());

    repositorioRefreshTokens.guardarActivo(
        newRefreshTokenHash, usuario.getId(), newRefreshTokenExpiresAt);

    TokenDTO accessToken = new TokenDTO(accessTokenString, accessTokenExpiresAt);
    TokenDTO refreshTokenDto = new TokenDTO(newRefreshTokenString, newRefreshTokenExpiresAt);
    UsuarioDTO usuarioDto = UsuarioDTO.from(usuario);

    return new AuthResponseDTO(accessToken, refreshTokenDto, usuarioDto);
  }
}
