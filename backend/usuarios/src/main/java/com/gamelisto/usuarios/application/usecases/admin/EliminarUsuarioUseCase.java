package com.gamelisto.usuarios.application.usecases.admin;

import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class EliminarUsuarioUseCase implements EliminarUsuarioHandle {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IUsuarioPublisher eventosPublisher;

  @Transactional
  public void execute(String idUsuario) {
    UsuarioId id = UsuarioId.fromString(idUsuario);

    Usuario usuario = buscarYSoftDelete(idUsuario, id);

    Usuario usuarioEliminado = repositorioUsuarios.save(usuario);

    publicarAfterCommit(() -> enviarEventoUsuarioEliminado(usuarioEliminado));
  }

  private @NonNull Usuario buscarYSoftDelete(String idUsuario, UsuarioId id) {
    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(
                () -> new ApplicationException("Usuario no encontrado con ID: " + idUsuario));

    usuario.delete();
    return usuario;
  }

  private void enviarEventoUsuarioEliminado(Usuario usuario) {
    UsuarioEliminado evento = UsuarioEliminado.of(usuario.getId().value().toString());
    eventosPublisher.publicarUsuarioEliminado(evento);
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
