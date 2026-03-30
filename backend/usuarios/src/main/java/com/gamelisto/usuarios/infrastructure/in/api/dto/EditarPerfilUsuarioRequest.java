package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record EditarPerfilUsuarioRequest(
    @Size(max = 500, message = "La URL del avatar no puede exceder 500 caracteres") String avatar) {
  public EditarPerfilUsuarioCommand toCommand(UUID usuarioId) {
    return new EditarPerfilUsuarioCommand(usuarioId, avatar);
  }
}
