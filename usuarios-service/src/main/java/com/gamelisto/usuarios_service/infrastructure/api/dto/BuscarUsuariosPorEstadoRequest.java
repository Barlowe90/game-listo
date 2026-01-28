package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.BuscarUsuariosPorEstadoCommand;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;

public record BuscarUsuariosPorEstadoRequest() {
  public BuscarUsuariosPorEstadoCommand tCommand(EstadoUsuario estadoUsuario) {
    return new BuscarUsuariosPorEstadoCommand(estadoUsuario);
  }
}
