package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.RestablecerContrasenaCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.PasswordHash;
import com.gamelisto.usuarios.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestablecerContrasenaUseCase {
  private final RepositorioUsuarios repositorioUsuarios;
  private final PasswordEncoder passwordEncoder;

  public RestablecerContrasenaUseCase(
      RepositorioUsuarios repositorioUsuarios, PasswordEncoder passwordEncoder) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void execute(RestablecerContrasenaCommand command) {
    Email email = Email.of(command.email());

    Usuario usuario =
        repositorioUsuarios
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con email: " + command.email()));

    TokenVerificacion tokenRecibido = TokenVerificacion.of(command.token());

    if (!usuario.tieneTokenRestablecimientoValido(tokenRecibido)) {
      throw new ApplicationException("Token de restablecimiento inválido o expirado");
    }

    String hashedPasswordNueva = passwordEncoder.encode(command.nuevaContrasena());
    usuario.changePasswordHash(PasswordHash.of(hashedPasswordNueva));
    usuario.invalidarTokenRestablecimiento();

    repositorioUsuarios.save(usuario);
  }
}
