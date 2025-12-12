package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.CambiarContraseñaCommand;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record CambiarContraseñaRequest (
    @NotBlank(message = "La contraseña actual es requerida")
    String contrasenaActual,

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    String contrasenaNueva
) {
    public CambiarContraseñaCommand toCommand(String usuarioId) {
        return new CambiarContraseñaCommand(usuarioId, contrasenaActual, contrasenaNueva);
    }
}
