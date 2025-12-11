package com.gamelisto.usuarios_service.infrastructure.exceptions;

public class UsuarioYaVerificadoException extends RuntimeException {

    private final String email;

    public UsuarioYaVerificadoException(String email) {
        super("El usuario con email " + email + " ya ha sido verificado");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
