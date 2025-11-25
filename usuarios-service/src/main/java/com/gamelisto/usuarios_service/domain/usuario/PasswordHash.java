package com.gamelisto.usuarios_service.domain.usuario;

public final class PasswordHash {
    
    private final String value;

    private PasswordHash(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El hash de contraseña no puede ser nulo o vacío");
        }
        this.value = value;
    }

    public static PasswordHash of(String value) {
        return new PasswordHash(value);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}
