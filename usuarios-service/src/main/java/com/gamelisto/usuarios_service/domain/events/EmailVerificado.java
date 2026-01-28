package com.gamelisto.usuarios_service.domain.events;

import java.time.Instant;

public record EmailVerificado(String usuarioId, String email, Instant occurredOn) {
  public static EmailVerificado of(String usuarioId, String email) {
    return new EmailVerificado(usuarioId, email, Instant.now());
  }
}
