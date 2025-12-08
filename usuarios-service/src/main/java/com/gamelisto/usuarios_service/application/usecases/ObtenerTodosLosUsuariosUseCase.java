package com.gamelisto.usuarios_service.application.usecases;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;

@Service
public class ObtenerTodosLosUsuariosUseCase {
    
    private final RepositorioUsuarios repositorioUsuarios;

    public ObtenerTodosLosUsuariosUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public List<UsuarioDTO> execute() {
        return repositorioUsuarios.findAll()
                .stream()
                .map(UsuarioDTO::from)
                .collect(Collectors.toList());
    }
}
