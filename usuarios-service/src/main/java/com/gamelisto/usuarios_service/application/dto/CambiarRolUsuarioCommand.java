package com.gamelisto.usuarios_service.application.dto;

import com.gamelisto.usuarios_service.domain.usuario.Rol;

public record CambiarRolUsuarioCommand(String usuarioId, Rol rol) {}
