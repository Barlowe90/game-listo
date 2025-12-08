package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.infrastructure.exceptions.UsuarioNoEncontradoException;

@Service
public class EliminarUsuarioUseCase {
    
    private final RepositorioUsuarios repositorioUsuarios;

    public EliminarUsuarioUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public void execute(String usuarioId) {
        UsuarioId id = UsuarioId.fromString(usuarioId);

        Usuario usuario = repositorioUsuarios
                .findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        repositorioUsuarios.delete(usuario);
    }
}
