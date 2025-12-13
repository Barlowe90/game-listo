package com.gamelisto.usuarios_service.application.dto;

import org.springframework.lang.NonNull;

public record VincularDiscordCommand(
    @NonNull String usuarioId,
    @NonNull String code,
    @NonNull String redirectUri
) {}
