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

    repositorioUsuarios.save(usuario);

    publicarAfterCommit(() ->
        usuario.drainEvents().forEach(e -> {
            if (e instanceof UsuarioEliminado ue) eventosPublisher.publicarUsuarioEliminado(ue);
        }));
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
