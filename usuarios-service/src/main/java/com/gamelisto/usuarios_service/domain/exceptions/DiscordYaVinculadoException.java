package com.gamelisto.usuarios_service.domain.exceptions;

public class DiscordYaVinculadoException extends RuntimeException {
    
    private final String discordId;

    public DiscordYaVinculadoException(String discordId) {
        super("La cuenta de Discord con ID '" + discordId + "' ya está vinculada a otro usuario");
        this.discordId = discordId;
    }

    public String getDiscordId() {
        return discordId;
    }
}
