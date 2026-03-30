package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.AuthResponseResult;
import com.gamelisto.usuarios.application.dto.LoginCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCase implements LoginUseHandle {

  private final RepositorioUsuarios repositorioUsuarios;
  private final PasswordEncoder passwordEncoder;
  private final AuthTokenService authTokenService;

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

    // Delegar la creación de tokens y persistencia al servicio centralizado
    return authTokenService.createAuthResponse(usuario);
  }
}
