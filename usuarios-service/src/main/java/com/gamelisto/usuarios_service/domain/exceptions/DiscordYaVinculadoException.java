package com.gamelisto.usuarios_service.domain.exceptions;

public class DiscordYaVinculadoException extends RuntimeException {
    
    public DiscordYaVinculadoException(String discordId) {
        super("La cuenta de Discord con ID '" + discordId + "' ya está vinculada a otro usuario");
    }
}
