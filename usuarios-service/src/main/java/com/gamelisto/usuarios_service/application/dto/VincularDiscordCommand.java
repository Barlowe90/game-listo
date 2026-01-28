package com.gamelisto.usuarios_service.application.dto;

public record VincularDiscordCommand(
    String usuarioId, String discordUserId, String discordUsername) {}
