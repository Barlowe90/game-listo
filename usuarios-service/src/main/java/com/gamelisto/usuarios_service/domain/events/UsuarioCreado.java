package com.gamelisto.usuarios_service.domain.events;

import java.time.Instant;

public record UsuarioCreado(
    String usuarioId,
    String username,
    String email,
    Instant occurredOn
) {
    public static UsuarioCreado of(String usuarioId, String username, String email) {
        return new UsuarioCreado(usuarioId, username, email, Instant.now());
    }
}
