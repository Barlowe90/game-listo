package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.CambiarCorreoCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CambiarCorreoRequest(
    @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido")
        String email) {
  public CambiarCorreoCommand toCommand(UUID usuarioId) {
    return new CambiarCorreoCommand(usuarioId, email);
  }
}
