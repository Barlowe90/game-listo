package com.gamelisto.usuarios_service.domain.events;

import java.time.Instant;

public record UsuarioEliminado(
    String usuarioId,
    Instant occurredOn
) {
    public static UsuarioEliminado of(String usuarioId) {
        return new UsuarioEliminado(usuarioId, Instant.now());
    }
}
