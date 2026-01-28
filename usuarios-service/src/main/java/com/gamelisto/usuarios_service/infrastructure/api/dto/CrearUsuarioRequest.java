package com.gamelisto.usuarios_service.infrastructure.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.gamelisto.usuarios_service.application.dto.CrearUsuarioCommand;

/**
 * DTO de entrada para la API REST - Crear usuario.
 * pertenece a la capa de infrastructure y representa
 * el contrato HTTP con el cliente externo.
 * 
 */
public record CrearUsuarioRequest(
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 30, message = "El username debe tener entre 3 y 30 caracteres")
    String username,
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    String email,
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    String password
) {
    public CrearUsuarioCommand toCommand() {
        return new CrearUsuarioCommand(username, email, password);
    }
}
