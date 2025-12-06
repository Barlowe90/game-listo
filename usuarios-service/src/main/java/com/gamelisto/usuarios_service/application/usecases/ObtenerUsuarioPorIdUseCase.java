package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;

@Service
public class ObtenerUsuarioPorIdUseCase {
    
    private final RepositorioUsuarios repositorioUsuarios;

    public ObtenerUsuarioPorIdUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    public UsuarioDTO execute(String usuarioId) {
        // TODO: Implementar lógica de obtención de usuario por ID
        // 1. Validar ID
        // 2. Buscar usuario
        // 3. Lanzar excepción si no existe
        // 4. Mapear a DTO
        // 5. Retornar
        return null;
    }
}
