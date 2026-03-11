package com.gamelisto.usuarios.application.dto;

import java.util.UUID;

public record CambiarCorreoCommand(UUID usuarioId, String email) {}
