package com.gamelisto.usuarios.application.dto;

import java.util.UUID;

public record EditarPerfilUsuarioCommand(UUID usuarioId, String avatar, String language) {}
