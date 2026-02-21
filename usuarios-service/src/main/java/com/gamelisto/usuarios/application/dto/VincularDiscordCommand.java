package com.gamelisto.usuarios.application.dto;

public record VincularDiscordCommand(
    String usuarioId, String discordUserId, String discordUsername) {}
