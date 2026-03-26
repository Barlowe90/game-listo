package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.AuthResponseResult;
import com.gamelisto.usuarios.application.dto.TokenDTO;
import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import com.gamelisto.usuarios.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.shared.auth.JwtProperties;
import com.gamelisto.usuarios.shared.auth.JwtUtils;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

  private final RepositorioRefreshTokens repositorioRefreshTokens;
  private final JwtProperties jwtProperties;

  /**
   * Genera un access token y un refresh token (además persiste el refresh token) y construye
   * el {@link AuthResponseResult} listo para devolver al cliente.
   */
  public AuthResponseResult createAuthResponse(Usuario usuario) {
    Jti jti = Jti.generate();

    String accessTokenString =
        JwtUtils.generateAccessToken(
            usuario, jti, jwtProperties.getSecret(), jwtProperties.getExpirationMs());
    Instant accessTokenExpiresAt = Instant.now().plusMillis(jwtProperties.getExpirationMs());

    TokenValue refreshTokenValue = TokenValue.generate();
    String refreshTokenString = refreshTokenValue.value();
    TokenHash refreshTokenHash = TokenHash.from(refreshTokenValue);
    Instant refreshTokenExpiresAt =
        Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs());

    repositorioRefreshTokens.guardarActivo(
        refreshTokenHash, usuario.getId(), refreshTokenExpiresAt);

    TokenDTO accessToken = new TokenDTO(accessTokenString, accessTokenExpiresAt);
    TokenDTO refreshTokenDto = new TokenDTO(refreshTokenString, refreshTokenExpiresAt);
    UsuarioResult usuarioResult = UsuarioResult.from(usuario);

    return new AuthResponseResult(accessToken, refreshTokenDto, usuarioResult);
  }
}

