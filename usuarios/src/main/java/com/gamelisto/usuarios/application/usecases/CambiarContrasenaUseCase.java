package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.CambiarContrasenaCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.PasswordHash;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CambiarContrasenaUseCase {

  private final RepositorioUsuarios repositorioUsuarios;
  private final PasswordEncoder passwordEncoder;

  public CambiarContrasenaUseCase(
      RepositorioUsuarios repositorioUsuarios, PasswordEncoder passwordEncoder) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void execute(CambiarContrasenaCommand command) {
    UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());

    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con ID: " + command.usuarioId()));

    if (!passwordEncoder.matches(command.contrasenaActual(), usuario.getPasswordHash().value())) {
      throw new ApplicationException("La contraseña actual no es correcta");
    }

    String hashedPasswordNueva = passwordEncoder.encode(command.contrasenaNueva());
    usuario.changePasswordHash(PasswordHash.of(hashedPasswordNueva));

    repositorioUsuarios.save(usuario);
  }
}
