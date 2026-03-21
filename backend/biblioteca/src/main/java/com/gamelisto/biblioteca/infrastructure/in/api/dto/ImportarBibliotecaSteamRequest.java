package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.ImportarBibliotecaSteamCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record ImportarBibliotecaSteamRequest(
    @NotBlank(message = "El steamId64 es obligatorio")
        @Pattern(regexp = "\\d{17}", message = "El steamId64 debe tener 17 digitos")
        String steamId64) {

  public ImportarBibliotecaSteamCommand toCommand(UUID userId) {
    return new ImportarBibliotecaSteamCommand(userId, steamId64.trim());
  }
}
