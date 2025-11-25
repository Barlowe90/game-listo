package com.gamelisto.usuarios_service.domain.usuario;

public final class DiscordUsername {
    
    private final String value;

    private DiscordUsername(String value) {
        if (value != null && !value.trim().isEmpty()) {
            String trimmedValue = value.trim();
            if (trimmedValue.length() > 100) {
                throw new IllegalArgumentException("El username de Discord no puede exceder 100 caracteres");
            }
            this.value = trimmedValue;
        } else {
            this.value = null;
        }
    }

    public static DiscordUsername of(String value) {
        return new DiscordUsername(value);
    }

    public static DiscordUsername empty() {
        return new DiscordUsername(null);
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
