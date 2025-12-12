package com.gamelisto.usuarios_service.application.dto;

public record CambiarContrasenaCommand (
    String usuarioId,
    String contrasenaActual,
    String contrasenaNueva
){}
