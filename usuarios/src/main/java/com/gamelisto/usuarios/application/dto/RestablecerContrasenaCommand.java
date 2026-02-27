package com.gamelisto.usuarios.application.dto;

public record RestablecerContrasenaCommand(String token, String nuevaContrasena, String email) {}
