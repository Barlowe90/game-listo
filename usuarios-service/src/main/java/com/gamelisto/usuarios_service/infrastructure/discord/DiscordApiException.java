package com.gamelisto.usuarios_service.infrastructure.discord;

public class DiscordApiException extends RuntimeException {
    
    public DiscordApiException(String message) {
        super(message);
    }
    
    public DiscordApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
