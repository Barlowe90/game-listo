package com.gamelisto.biblioteca.domain;

import java.util.Objects;

public final class GameId {

    private final Long value;

    private GameId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("El id del juego no puede ser nulo");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("El id del juego debe ser un entero positivo");
        }
        this.value = value;
    }

    public static GameId of(Long value) {
        return new GameId(value);
    }

    public static GameId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El id del juego no puede ser nulo o vacío");
        }
        try {
            return new GameId(Long.parseLong(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de id de juego inválido: " + value, e);
        }
    }

    public Long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GameId gameId = (GameId) o;
        return Objects.equals(value, gameId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
