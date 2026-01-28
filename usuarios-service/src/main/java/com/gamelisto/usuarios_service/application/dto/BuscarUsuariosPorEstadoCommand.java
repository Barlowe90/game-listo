package com.gamelisto.usuarios_service.application.dto;

import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;

public record BuscarUsuariosPorEstadoCommand(
        EstadoUsuario estado) {
}
