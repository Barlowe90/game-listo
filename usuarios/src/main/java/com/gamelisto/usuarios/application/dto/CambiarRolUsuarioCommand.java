package com.gamelisto.usuarios.application.dto;

import com.gamelisto.usuarios.domain.usuario.Rol;

public record CambiarRolUsuarioCommand(String usuarioId, Rol rol) {}
