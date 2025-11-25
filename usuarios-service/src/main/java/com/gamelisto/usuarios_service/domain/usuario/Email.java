package com.gamelisto.usuarios_service.domain.usuario;

import java.util.regex.Pattern;

public final class Email {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private final String value;

    private Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }
        
        String normalizedEmail = value.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("El formato del email es inválido: " + value);
        }
        
        if (normalizedEmail.length() > 255) {
            throw new IllegalArgumentException("El email no puede exceder 255 caracteres");
        }
        
        this.value = normalizedEmail;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
