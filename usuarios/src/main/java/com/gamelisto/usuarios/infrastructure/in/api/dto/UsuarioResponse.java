package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.UsuarioResult;

public record UsuarioResponse(
    String id,
    String username,
    String email,
    String avatar,
    String role,
    String language,
    String status,
    String discordUserId,
    String discordUsername) {
  public static UsuarioResponse from(UsuarioResult dto) {
    return new UsuarioResponse(
        dto.id(),
        dto.username(),
        dto.email(),
        dto.avatar(),
        dto.role(),
        dto.language(),
        dto.status(),
        dto.discordUserId(),
        dto.discordUsername());
  }
}
