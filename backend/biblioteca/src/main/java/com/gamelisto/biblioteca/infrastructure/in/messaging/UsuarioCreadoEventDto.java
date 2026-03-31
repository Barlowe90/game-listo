package com.gamelisto.biblioteca.infrastructure.in.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DTO que mapea el evento UsuarioCreado publicado por el servicio usuarios. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioCreadoEventDto(
    String usuarioId, String username, String avatar, String discordUserId) {
  public UsuarioCreadoEventDto(String usuarioId, String username, String avatar) {
    this(usuarioId, username, avatar, null);
  }
}
