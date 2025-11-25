package com.gamelisto.usuarios_service.domain.usuario;

public final class DiscordUserId {
    
    private final String value;

    private DiscordUserId(String value) {
        if (value != null && !value.trim().isEmpty()) {
            String trimmedValue = value.trim();
            if (trimmedValue.length() > 100) {
                throw new IllegalArgumentException("El ID de Discord no puede exceder 100 caracteres");
            }
            this.value = trimmedValue;
        } else {
            this.value = null;
        }
    }

    public static DiscordUserId of(String value) {
        return new DiscordUserId(value);
    }

    public static DiscordUserId empty() {
        return new DiscordUserId(null);
    }

    public String value() {
        return value;
    }

    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public String toString() {
        return value != null ? value : "[NO VINCULADO]";
    }
}
