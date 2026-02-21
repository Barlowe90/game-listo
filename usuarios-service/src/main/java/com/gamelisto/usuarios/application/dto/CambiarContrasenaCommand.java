package com.gamelisto.usuarios.application.dto;

public record CambiarContrasenaCommand(
    String usuarioId, String contrasenaActual, String contrasenaNueva) {}
