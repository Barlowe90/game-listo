package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.VerificarEmailCommand;
import jakarta.validation.constraints.NotBlank;

public record VerificarEmailRequest(
    @NotBlank(message = "El token de verificación es obligatorio") String token) {
  public VerificarEmailCommand toCommand() {
    return new VerificarEmailCommand(token);
  }
}
