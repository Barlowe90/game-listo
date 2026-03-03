package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.RestablecerContrasenaCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestablecerContrasenaRequest(
    @NotBlank(message = "El token es obligatorio") String token,
    @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String nuevaContrasena,
    @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido")
        String email) {
  public RestablecerContrasenaCommand toCommand() {
    return new RestablecerContrasenaCommand(token, nuevaContrasena, email);
  }
}
