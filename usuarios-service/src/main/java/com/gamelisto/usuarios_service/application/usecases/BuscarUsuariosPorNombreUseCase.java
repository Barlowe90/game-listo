package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Username;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;

@Service
public class BuscarUsuariosPorNombreUseCase {

    private final RepositorioUsuarios repositorioUsuarios;

    public BuscarUsuariosPorNombreUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional(readOnly = true)
    public UsuarioDTO execute(String username) {
        Username u = Username.of(username);

        Usuario usuario = repositorioUsuarios.findByUsername(u)
                .orElseThrow(() -> new UsuarioNoEncontradoException(username));

        return UsuarioDTO.from(usuario);
    }
}
