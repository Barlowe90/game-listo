package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Avatar;
import com.gamelisto.usuarios_service.domain.usuario.Idioma;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.infrastructure.exceptions.UsuarioNoEncontradoException;

@Service
public class EditarPerfilUsuarioUseCase {
    
    
    private final RepositorioUsuarios repositorioUsuarios;

    public EditarPerfilUsuarioUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
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
            } else {
                usuario.disableNotifications();
            }
        }
    
        Usuario usuarioEditado = repositorioUsuarios.save(usuario);

        return UsuarioDTO.from(usuarioEditado);
    }
}
