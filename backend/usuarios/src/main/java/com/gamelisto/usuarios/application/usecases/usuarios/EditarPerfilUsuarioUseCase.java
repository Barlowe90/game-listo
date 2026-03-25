package com.gamelisto.usuarios.application.usecases.usuarios;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.events.UsuarioActualizado;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Avatar;
import com.gamelisto.usuarios.domain.usuario.Idioma;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class EditarPerfilUsuarioUseCase implements EditarPerfilUsuarioHandle {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IUsuarioPublisher usuarioPublisher;

  @Transactional
  public UsuarioResult execute(EditarPerfilUsuarioCommand command) {
    UsuarioId usuarioId = UsuarioId.of(command.usuarioId());
    boolean debePublicarActualizacionAvatar = command.avatar() != null;

    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con ID: " + command.usuarioId()));

    if (command.avatar() != null) {
      usuario.changeAvatar(Avatar.of(command.avatar()));
    }

    if (command.language() != null) {
      try {
        usuario.changeLanguage(Idioma.valueOf(command.language()));
      } catch (IllegalArgumentException e) {
        throw new ApplicationException("Idioma inválido: " + command.language());
      }
    }

    Usuario usuarioEditado = repositorioUsuarios.save(usuario);

    if (debePublicarActualizacionAvatar) {
      publicarAfterCommit(() -> publicarUsuarioActualizado(usuarioEditado));
    }

    return UsuarioResult.from(usuarioEditado);
  }

  private void publicarUsuarioActualizado(Usuario usuario) {
    UsuarioActualizado evento =
        UsuarioActualizado.of(
            usuario.getId().value().toString(),
            usuario.getUsername().value(),
            usuario.getAvatar().url(),
            usuario.getDiscordUserId().value());
    usuarioPublisher.publicarUsuarioActualizado(evento);
  }

  private static void publicarAfterCommit(Runnable action) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              action.run();
            }
          });
    } else {
      action.run();
    }
  }
}
