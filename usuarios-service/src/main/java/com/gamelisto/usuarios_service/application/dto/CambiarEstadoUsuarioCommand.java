package com.gamelisto.usuarios_service.application.dto;

import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;

public record CambiarEstadoUsuarioCommand(String usuarioId, EstadoUsuario estadoUsuario) {}
