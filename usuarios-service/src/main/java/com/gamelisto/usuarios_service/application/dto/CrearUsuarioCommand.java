package com.gamelisto.usuarios_service.application.dto;

public record CrearUsuarioCommand(
    String username,
    String email,
    String password
) {}