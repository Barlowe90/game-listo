package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.CambiarCorreoCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CambiarCorreoUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public CambiarCorreoUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional
  public void execute(CambiarCorreoCommand command) {
    Email nuevoEmail = Email.of(command.email());
    UsuarioId id = UsuarioId.fromString(command.usuarioId());

    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con ID: " + command.usuarioId()));

    // Validar que el nuevo email no esté ya registrado por otro usuario
    repositorioUsuarios
        .findByEmail(nuevoEmail)
        .filter(u -> !u.getId().equals(id))
        .ifPresent(
            u -> {
              throw new ApplicationException(
                  "El email '" + command.email() + "' ya está registrado");
            });

    // Solo procesar si el email realmente cambió
    if (!usuario.getEmail().equals(nuevoEmail)) {
      usuario.changeEmail(nuevoEmail);
      usuario.marcarPendienteVerificacion();
      usuario.generarTokenVerificacion();

      repositorioUsuarios.save(usuario);
    }
  }
}
