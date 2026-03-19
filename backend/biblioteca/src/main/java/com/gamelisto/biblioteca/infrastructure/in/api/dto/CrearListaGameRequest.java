package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.CrearListaGameCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CrearListaGameRequest(
    @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres")
        String nombre,
    @NotBlank(message = "El tipo es obligatorio")
        @Pattern(
            regexp = "PERSONALIZADA|OFICIAL",
            message = "El tipo debe ser PERSONALIZADA u OFICIAL")
        String tipo) {

  public CrearListaGameCommand toCommand(UUID userId) {
    return new CrearListaGameCommand(userId, nombre, tipo);
  }
}
