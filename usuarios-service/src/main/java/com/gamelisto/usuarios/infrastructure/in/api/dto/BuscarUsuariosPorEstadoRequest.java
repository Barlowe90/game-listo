package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.BuscarUsuariosPorEstadoCommand;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;

public record BuscarUsuariosPorEstadoRequest() {
  public BuscarUsuariosPorEstadoCommand tCommand(EstadoUsuario estadoUsuario) {
    return new BuscarUsuariosPorEstadoCommand(estadoUsuario);
  }
}
