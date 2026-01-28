package com.gamelisto.usuarios_service.application.dto;

public record RestablecerContrasenaCommand(String token, String nuevaContrasena, String email) {}
