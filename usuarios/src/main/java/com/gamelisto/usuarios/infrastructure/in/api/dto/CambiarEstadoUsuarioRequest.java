package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.CambiarEstadoUsuarioCommand;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CambiarEstadoUsuarioRequest(
    @NotNull(message = "El estado del usuario es obligatorio") @Pattern(
            regexp = "ACTIVO|SUSPENDIDO",
            message = "El estado del usuario debe ser ACTIVO o SUSPENDIDO")
        String estadoUsuario) {
  public CambiarEstadoUsuarioCommand toCommand(String usuarioId) {
    EstadoUsuario estado = EstadoUsuario.valueOf(estadoUsuario.toUpperCase());
    return new CambiarEstadoUsuarioCommand(usuarioId, estado);
  }
}
