package com.gamelisto.usuarios.application.usecases.usuarios;

import com.gamelisto.usuarios.application.dto.CambiarCorreoCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CambiarCorreoUseCase implements CambiarCorreoHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional
  public void execute(CambiarCorreoCommand command) {
    Email nuevoEmail = Email.of(command.email());
    UsuarioId id = UsuarioId.of(command.usuarioId());

    Usuario usuario = obtenerUsuario(command, id);

    comprobarSiElNuevoCorreoYaExiste(command, nuevoEmail, id);

    cambiarEmailAndEnviarTokenVerificacion(usuario, nuevoEmail);

    repositorioUsuarios.save(usuario);
  }

  private static void cambiarEmailAndEnviarTokenVerificacion(Usuario usuario, Email nuevoEmail) {
    usuario.changeEmail(nuevoEmail);
    usuario.marcarPendienteVerificacion();
    usuario.generarTokenVerificacion();
  }

  private void comprobarSiElNuevoCorreoYaExiste(
      CambiarCorreoCommand command, Email nuevoEmail, UsuarioId id) {
    repositorioUsuarios
        .findByEmail(nuevoEmail)
        .filter(u -> !u.getId().equals(id))
        .ifPresent(
            u -> {
              throw new ApplicationException(
                  "El email '" + command.email() + "' ya está registrado");
            });
  }

  private @NonNull Usuario obtenerUsuario(CambiarCorreoCommand command, UsuarioId id) {
    return repositorioUsuarios
        .findById(id)
        .orElseThrow(
            () -> new ApplicationException("Usuario no encontrado con ID: " + command.usuarioId()));
  }
}
