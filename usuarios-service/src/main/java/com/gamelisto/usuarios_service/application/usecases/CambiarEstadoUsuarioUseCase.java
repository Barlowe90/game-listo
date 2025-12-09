package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;

import com.gamelisto.usuarios_service.application.dto.CambiarEstadoUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.infrastructure.exceptions.UsuarioNoEncontradoException;

import jakarta.transaction.Transactional;

@Service
public class CambiarEstadoUsuarioUseCase {
    
    private final RepositorioUsuarios repositorioUsuarios;

    public CambiarEstadoUsuarioUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public UsuarioDTO execute(CambiarEstadoUsuarioCommand command) {
        UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());

        Usuario usuario = repositorioUsuarios
                .findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(command.usuarioId()));

        String nuevoEstado = command.estadoUsuario();
        
        if ("SUSPENDIDO".equals(nuevoEstado)) {
            usuario.suspend();
        } else if ("ACTIVO".equals(nuevoEstado)) {
            usuario.activate();
        }
        
        Usuario usuarioEditado = repositorioUsuarios.save(usuario);

        return UsuarioDTO.from(usuarioEditado);
    }

}
