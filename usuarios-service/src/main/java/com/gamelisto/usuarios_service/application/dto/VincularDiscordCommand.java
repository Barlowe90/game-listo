package com.gamelisto.usuarios_service.application.dto;

public record VincularDiscordCommand(
    String usuarioId,
    String code,
    String redirectUri
) {}
