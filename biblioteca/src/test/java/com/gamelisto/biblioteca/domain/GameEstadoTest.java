package com.gamelisto.biblioteca.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GameEstado rating validation")
class GameEstadoTest {

  @Test
  @DisplayName("debe aceptar rating válido 0.0, 0.25, 5.0, 0.1 y rechazar 5.1")
  void ratingValidation() {
    UUID userId = UUID.randomUUID();
    UUID gameId = UUID.randomUUID();

    // valores válidos
    GameEstado.create(userId, gameId, Estado.DESEADO, 0.0);
    GameEstado.create(userId, gameId, Estado.PENDIENTE, 0.25);
    GameEstado.create(userId, gameId, Estado.COMPLETADO, 5.0);
    GameEstado.create(userId, gameId, Estado.PENDIENTE, 0.1);

    // inválidos
    assertThrows(
        DomainException.class, () -> GameEstado.create(userId, gameId, Estado.PENDIENTE, -1));
    assertThrows(
        DomainException.class, () -> GameEstado.create(userId, gameId, Estado.PENDIENTE, 5.1));
  }
}
