package com.gamelisto.usuarios_service.infrastructure.exceptions;

public class EmailYaRegistradoException extends RuntimeException {
    
    private final String email;
    
    public EmailYaRegistradoException(String email) {
        super(String.format("El email '%s' ya está registrado", email));
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
}