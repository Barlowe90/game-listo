package com.gamelisto.usuarios.application.dto;

import java.util.UUID;

public record CambiarContrasenaCommand(
    UUID usuarioId, String contrasenaActual, String contrasenaNueva) {}
