package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.SolicitarRestablecimientoCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitarRestablecimientoRequest(
    @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido")
        String email) {
  public SolicitarRestablecimientoCommand toCommand() {
    return new SolicitarRestablecimientoCommand(email);
  }
}
