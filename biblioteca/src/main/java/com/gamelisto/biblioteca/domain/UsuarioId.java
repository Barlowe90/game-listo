package com.gamelisto.biblioteca.domain;

import java.util.Objects;
import java.util.UUID;

public final class UsuarioId {

    private final UUID value;

    private UsuarioId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("El id de usuario no puede ser nulo");
        }
        this.value = value;
    }

    public static UsuarioId of(UUID value) {
        return new UsuarioId(value);
    }

    public static UsuarioId generate() {
        return new UsuarioId(UUID.randomUUID());
    }

    public static UsuarioId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El id de usuario no puede ser nulo o vacío");
        }
        try {
            return new UsuarioId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formato de UUID inválido: " + value, e);
        }
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UsuarioId usuarioId = (UsuarioId) o;
        return Objects.equals(value, usuarioId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
