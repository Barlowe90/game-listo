package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.EditarListaGameCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record EditarListaGameRequest(
    @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres")
        String nombre) {

  public EditarListaGameCommand toCommand(UUID userId, String listaId) {
    return new EditarListaGameCommand(userId, listaId, nombre);
  }
}
