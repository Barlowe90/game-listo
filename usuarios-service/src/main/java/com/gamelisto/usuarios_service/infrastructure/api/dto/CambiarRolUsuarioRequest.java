package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.CambiarRolUsuarioCommand;
import com.gamelisto.usuarios_service.domain.usuario.Rol;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CambiarRolUsuarioRequest(
    @NotNull(message = "El rol del usuario es obligatorio")
        @Pattern(regexp = "USER|ADMIN", message = "El rol del usuario debe ser USER o ADMIN")
        String rol) {
  public CambiarRolUsuarioCommand toCommand(String usuarioId) {
    Rol nuevoRol = Rol.valueOf(rol.toUpperCase());
    return new CambiarRolUsuarioCommand(usuarioId, nuevoRol);
  }
}
