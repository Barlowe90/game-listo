package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.CambiarContrasenaCommand;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record CambiarContrasenaRequest (
    @NotBlank(message = "La contraseña actual es requerida")
    String contrasenaActual,

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    String contrasenaNueva
) {
    public CambiarContrasenaCommand toCommand(String usuarioId) {
        return new CambiarContrasenaCommand(usuarioId, contrasenaActual, contrasenaNueva);
    }
}
