package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarUsuarioUseCase {

  private static final String ROUTING_KEY_SUFFIX = "usuario.eliminado";
  private final RepositorioUsuarios repositorioUsuarios;
  private final IUsuarioPublisher eventosPublisher;

  public EliminarUsuarioUseCase(
      RepositorioUsuarios repositorioUsuarios, IUsuarioPublisher eventosPublisher) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.eventosPublisher = eventosPublisher;
  }

  @Transactional
  public void execute(String idUsuario) {
    UsuarioId id = UsuarioId.fromString(idUsuario);
    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(
                () -> new ApplicationException("Usuario no encontrado con ID: " + idUsuario));

    usuario.delete();

    Usuario usuarioEliminado = repositorioUsuarios.save(usuario);

    enviarEventoUsuarioEliminado(usuarioEliminado);
  }

  private void enviarEventoUsuarioEliminado(Usuario usuario) {
    UsuarioEliminado evento = UsuarioEliminado.of(usuario.getId().value().toString());
    eventosPublisher.publish(ROUTING_KEY_SUFFIX, evento);
  }
}
