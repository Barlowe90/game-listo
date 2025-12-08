package com.gamelisto.usuarios_service.application.dto;

public record EditarPerfilUsuarioCommand(
    String usuarioId,
    String avatar,
    String language,
    Boolean notificationsActive
) {}
