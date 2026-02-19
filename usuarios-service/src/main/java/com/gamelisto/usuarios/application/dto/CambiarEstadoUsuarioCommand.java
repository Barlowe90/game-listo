package com.gamelisto.usuarios.application.dto;

import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;

public record CambiarEstadoUsuarioCommand(String usuarioId, EstadoUsuario estadoUsuario) {}
