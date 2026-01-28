package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.ReenviarVerificacionCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReenviarVerificacionRequest(
    @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido")
        String email) {
  public ReenviarVerificacionCommand toCommand() {
    return new ReenviarVerificacionCommand(email);
  }
}
