package com.gamelisto.usuarios_service.domain.events;

import java.time.Instant;

public record UsuarioDesactivaNotificaciones(String usuarioId, Instant occurredOn) {
  public static UsuarioDesactivaNotificaciones of(String usuarioId) {
    return new UsuarioDesactivaNotificaciones(usuarioId, Instant.now());
  }
}
