package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;

public record UsuarioResponse(
    String id,
    String username,
    String email,
    String avatar,
    String role,
    String language,
    boolean notificationsActive,
    String status,
    String discordUserId,
    String discordUsername) {
  public static UsuarioResponse from(UsuarioDTO dto) {
    return new UsuarioResponse(
        dto.id(),
        dto.username(),
        dto.email(),
        dto.avatar(),
        dto.role(),
        dto.language(),
        dto.notificationsActive(),
        dto.status(),
        dto.discordUserId(),
        dto.discordUsername());
  }
}
