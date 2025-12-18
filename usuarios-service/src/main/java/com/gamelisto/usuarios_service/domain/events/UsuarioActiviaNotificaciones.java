package com.gamelisto.usuarios_service.domain.events;

import java.time.Instant;

public record UsuarioActiviaNotificaciones(
    String usuarioId,
    Instant occurredOn
) {
    public static UsuarioActiviaNotificaciones of(String usuarioId) {
        return new UsuarioActiviaNotificaciones(usuarioId, Instant.now());
    }
}

