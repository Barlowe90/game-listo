package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditarPerfilUsuarioRequest(
    @Size(max = 500, message = "La URL del avatar no puede exceder 500 caracteres") String avatar,
    @Pattern(regexp = "ESP|ENG", message = "El idioma debe ser ESP o ENG") String language) {
  public EditarPerfilUsuarioCommand toCommand(String usuarioId) {
    return new EditarPerfilUsuarioCommand(usuarioId, avatar, language);
  }
}
