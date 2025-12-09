package com.gamelisto.usuarios_service.application.dto;

public record CambiarEstadoUsuarioCommand (
    String usuarioId,
    String estadoUsuario
) {}
