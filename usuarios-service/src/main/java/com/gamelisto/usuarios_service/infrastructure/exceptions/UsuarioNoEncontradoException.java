package com.gamelisto.usuarios_service.infrastructure.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {

    private final String usuarioId;

    public UsuarioNoEncontradoException(String usuarioId) {
        super(String.format("Usuario no encontrado con id: %s ", usuarioId));
        this.usuarioId = usuarioId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }
    
}
