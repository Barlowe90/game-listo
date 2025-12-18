package com.gamelisto.usuarios_service.domain.events;

import java.time.Instant;

public record UsuarioActualizado(
    String usuarioId,
    String username,
    Instant occurredOn
) {
    public static UsuarioActualizado of(String usuarioId, String username) {
        return new UsuarioActualizado(usuarioId, username, Instant.now());
    }
}
