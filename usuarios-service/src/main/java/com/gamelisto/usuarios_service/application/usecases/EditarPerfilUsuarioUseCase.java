package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Avatar;
import com.gamelisto.usuarios_service.domain.usuario.Idioma;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.domain.events.UsuarioActiviaNotificaciones;
import com.gamelisto.usuarios_service.domain.events.UsuarioDesactivaNotificaciones;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;

@Service
public class EditarPerfilUsuarioUseCase {
    
    
    private final RepositorioUsuarios repositorioUsuarios;
    private final IUsuarioPublisher usuarioPublisher;
    private static final String ROUTING_KEY_SUFFIX_ACTIVAR = "usuario.activaNotificaciones";
    private static final String ROUTING_KEY_SUFFIX_DESACTIVAR = "usuario.desactivaNotificaciones";

    public EditarPerfilUsuarioUseCase(RepositorioUsuarios repositorioUsuarios, IUsuarioPublisher usuarioPublisher) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.usuarioPublisher = usuarioPublisher;
    }

    @Transactional
    public UsuarioDTO execute(EditarPerfilUsuarioCommand command) {
        UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());
        
        Usuario usuario = repositorioUsuarios
                .findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(command.usuarioId()));

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
        UsuarioDesactivaNotificaciones evento = UsuarioDesactivaNotificaciones.of(
            usuario.getId().value().toString()
        );
        usuarioPublisher.publish(ROUTING_KEY_SUFFIX_DESACTIVAR, evento);
    }

    private void avisarColaUsarioActivaNotificaciones(Usuario usuario) {
        UsuarioActiviaNotificaciones evento = UsuarioActiviaNotificaciones.of(
            usuario.getId().value().toString()
        );
        usuarioPublisher.publish(ROUTING_KEY_SUFFIX_ACTIVAR, evento);
    }
}
