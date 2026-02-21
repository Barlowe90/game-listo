package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.events.UsuarioActiviaNotificaciones;
import com.gamelisto.usuarios.domain.events.UsuarioDesactivaNotificaciones;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Avatar;
import com.gamelisto.usuarios.domain.usuario.Idioma;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarPerfilUsuarioUseCase {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IUsuarioPublisher usuarioPublisher;
  private static final String ROUTING_KEY_SUFFIX_ACTIVAR = "usuario.activaNotificaciones";
  private static final String ROUTING_KEY_SUFFIX_DESACTIVAR = "usuario.desactivaNotificaciones";

  public EditarPerfilUsuarioUseCase(
      RepositorioUsuarios repositorioUsuarios, IUsuarioPublisher usuarioPublisher) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.usuarioPublisher = usuarioPublisher;
  }

  @Transactional
  public UsuarioDTO execute(EditarPerfilUsuarioCommand command) {
    UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());

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
      usuario.changeLanguage(Idioma.valueOf(command.language()));
    }

    if (command.notificationsActive() != null) {
      if (command.notificationsActive()) {
        usuario.enableNotifications();
        avisarColaUsarioActivaNotificaciones(usuario);
      } else {
        usuario.disableNotifications();
        avisarColaUsarioDesactivaNotificaciones(usuario);
      }
    }

    Usuario usuarioEditado = repositorioUsuarios.save(usuario);

    return UsuarioDTO.from(usuarioEditado);
  }

  private void avisarColaUsarioDesactivaNotificaciones(Usuario usuario) {
    UsuarioDesactivaNotificaciones evento =
        UsuarioDesactivaNotificaciones.of(usuario.getId().value().toString());
    usuarioPublisher.publish(ROUTING_KEY_SUFFIX_DESACTIVAR, evento);
  }

  private void avisarColaUsarioActivaNotificaciones(Usuario usuario) {
    UsuarioActiviaNotificaciones evento =
        UsuarioActiviaNotificaciones.of(usuario.getId().value().toString());
    usuarioPublisher.publish(ROUTING_KEY_SUFFIX_ACTIVAR, evento);
  }
}
