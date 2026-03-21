package com.gamelisto.social.infrastructure.out.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/** DTO que mapea el evento UsuarioCreado publicado por el servicio usuarios. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioCreadoEventDto(
    UUID usuarioId,
    String username,
    String avatar,
    String discordUserId,
    String discordUsername) {
  public UsuarioCreadoEventDto(UUID usuarioId, String username, String avatar) {
    this(usuarioId, username, avatar, null, null);
  }
}
