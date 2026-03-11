package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.AuthResponseResult;
import com.gamelisto.usuarios.application.dto.LoginCommand;
import com.gamelisto.usuarios.application.dto.TokenDTO;
import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import com.gamelisto.usuarios.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.shared.auth.JwtProperties;
import com.gamelisto.usuarios.shared.auth.JwtUtils;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCase implements LoginUseHandle {

  private final RepositorioUsuarios repositorioUsuarios;
  private final RepositorioRefreshTokens repositorioRefreshTokens;
  private final PasswordEncoder passwordEncoder;
  private final JwtProperties jwtProperties;

  @Transactional
  public AuthResponseResult execute(LoginCommand command) {

    Email email = Email.of(command.email());
    Usuario usuario =
        repositorioUsuarios
            .findByEmail(email)
            .orElseThrow(() -> new ApplicationException("Email o contraseña incorrectos"));

    if (!passwordEncoder.matches(command.password(), usuario.getPasswordHash().value())) {
      throw new ApplicationException("Email o contraseña incorrectos");
    }

    if (usuario.getStatus() != EstadoUsuario.ACTIVO) {
      throw new ApplicationException("Usuario no activo. Estado: " + usuario.getStatus().name());
    }

    // 4. Generar access token JWT
    Jti jti = Jti.generate();
    String accessTokenString =
        JwtUtils.generateAccessToken(
            usuario, jti, jwtProperties.getSecret(), jwtProperties.getExpirationMs());
    Instant accessTokenExpiresAt = Instant.now().plusMillis(jwtProperties.getExpirationMs());

    // 5. Generar refresh token
    TokenValue refreshTokenValue = TokenValue.generate();
    String refreshTokenString = refreshTokenValue.value();
    TokenHash refreshTokenHash = TokenHash.from(refreshTokenValue);
    Instant refreshTokenExpiresAt =
        Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs());

    // 6. Guardar refresh token en Redis
    repositorioRefreshTokens.guardarActivo(
        refreshTokenHash, usuario.getId(), refreshTokenExpiresAt);

    // 7. Construir respuesta
    TokenDTO accessToken = new TokenDTO(accessTokenString, accessTokenExpiresAt);
    TokenDTO refreshTokenDto = new TokenDTO(refreshTokenString, refreshTokenExpiresAt);
    UsuarioResult usuarioResult = UsuarioResult.from(usuario);

    return new AuthResponseResult(accessToken, refreshTokenDto, usuarioResult);
  }
}
