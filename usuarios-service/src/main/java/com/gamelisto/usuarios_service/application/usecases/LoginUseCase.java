package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.AuthResponseDTO;
import com.gamelisto.usuarios_service.application.dto.LoginCommand;
import com.gamelisto.usuarios_service.application.dto.TokenDTO;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.CredencialesInvalidasException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoActivoException;
import com.gamelisto.usuarios_service.domain.refreshtoken.Jti;
import com.gamelisto.usuarios_service.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios_service.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.infrastructure.auth.JwtProperties;
import com.gamelisto.usuarios_service.infrastructure.auth.JwtUtils;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUseCase {

  private final RepositorioUsuarios repositorioUsuarios;
  private final RepositorioRefreshTokens repositorioRefreshTokens;
  private final PasswordEncoder passwordEncoder;
  private final JwtProperties jwtProperties;

  public LoginUseCase(
      RepositorioUsuarios repositorioUsuarios,
      RepositorioRefreshTokens repositorioRefreshTokens,
      PasswordEncoder passwordEncoder,
      JwtProperties jwtProperties) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.repositorioRefreshTokens = repositorioRefreshTokens;
    this.passwordEncoder = passwordEncoder;
    this.jwtProperties = jwtProperties;
  }

  @Transactional
  public AuthResponseDTO execute(LoginCommand command) {

    Email email = Email.of(command.email());
    Usuario usuario =
        repositorioUsuarios
            .findByEmail(email)
            .orElseThrow(
                () -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

    if (!passwordEncoder.matches(command.password(), usuario.getPasswordHash().value())) {
      throw new CredencialesInvalidasException("Email o contraseña incorrectos");
    }

    if (usuario.getStatus() != EstadoUsuario.ACTIVO) {
      throw new UsuarioNoActivoException(
          "Usuario no activo. Estado: " + usuario.getStatus().name());
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
    UsuarioDTO usuarioDto = UsuarioDTO.from(usuario);

    return new AuthResponseDTO(accessToken, refreshTokenDto, usuarioDto);
  }
}
