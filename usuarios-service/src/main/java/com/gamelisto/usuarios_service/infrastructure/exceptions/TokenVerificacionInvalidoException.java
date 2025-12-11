package com.gamelisto.usuarios_service.infrastructure.exceptions;

public class TokenVerificacionInvalidoException extends RuntimeException {

    private final String token;

    public TokenVerificacionInvalidoException(String token) {
        super("El token de verificación es inválido o ha expirado");
        this.token = token;
    }

    public TokenVerificacionInvalidoException(String token, String message) {
        super(message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
