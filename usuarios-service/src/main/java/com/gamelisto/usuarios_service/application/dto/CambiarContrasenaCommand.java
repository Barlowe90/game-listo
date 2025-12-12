package com.gamelisto.usuarios_service.application.dto;

public record CambiarContraseñaCommand (
    String usuarioId,
    String contrasenaActual,
    String contrasenaNueva
){}
