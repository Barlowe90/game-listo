package com.gamelisto.usuarios.infrastructure.out.messaging.dto;

import java.time.Instant;

public record UsuarioMensaje<T>(
    String eventId, String eventType, String service, Instant timestamp, T data) {
  public static <T> UsuarioMensaje<T> of(String eventType, T data) {
    return new UsuarioMensaje<>(
        java.util.UUID.randomUUID().toString(), eventType, "usuarios", Instant.now(), data);
  }
}
