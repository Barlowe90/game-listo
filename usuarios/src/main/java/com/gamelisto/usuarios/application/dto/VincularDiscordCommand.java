package com.gamelisto.usuarios.application.dto;

import java.util.UUID;

public record VincularDiscordCommand(
    UUID usuarioId, String discordUserId, String discordUsername) {}
