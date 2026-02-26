package com.gamelisto.usuarios.application.dto;

import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;

public record BuscarUsuariosPorEstadoCommand(EstadoUsuario estado) {}
