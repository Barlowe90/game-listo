package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.CambiarCorreoCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CambiarCorreoRequest(
    @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido")
        String email) {
  public CambiarCorreoCommand toCommand(String usuarioId) {
    return new CambiarCorreoCommand(usuarioId, email);
  }
}
