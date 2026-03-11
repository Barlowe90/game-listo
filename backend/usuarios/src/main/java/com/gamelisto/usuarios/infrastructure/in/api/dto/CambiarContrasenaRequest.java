package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.CambiarContrasenaCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CambiarContrasenaRequest(
    @NotBlank(message = "La contraseña actual es requerida") String contrasenaActual,
    @NotBlank(message = "La nueva contraseña es requerida")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        String contrasenaNueva) {
  public CambiarContrasenaCommand toCommand(UUID usuarioId) {
    return new CambiarContrasenaCommand(usuarioId, contrasenaActual, contrasenaNueva);
  }
}
