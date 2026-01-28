package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.EditarPerfilUsuarioCommand;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditarPerfilUsuarioRequest(
    @Size(max = 500, message = "La URL del avatar no puede exceder 500 caracteres") String avatar,
    @Pattern(regexp = "ESP|ENG", message = "El idioma debe ser ESP o ENG") String language,
    Boolean notificationsActive) {
  public EditarPerfilUsuarioCommand toCommand(String usuarioId) {
    return new EditarPerfilUsuarioCommand(usuarioId, avatar, language, notificationsActive);
  }
}
