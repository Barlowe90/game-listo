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
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    Long gameId = Long.MAX_VALUE;

    // valores válidos
    GameEstado.create(userId, GameId.of(gameId), Estado.DESEADO, Rating.of(0.0));
    GameEstado.create(userId, GameId.of(gameId), Estado.PENDIENTE, Rating.of(0.25));
    GameEstado.create(userId, GameId.of(gameId), Estado.COMPLETADO, Rating.of(5.0));
    GameEstado.create(userId, GameId.of(gameId), Estado.PENDIENTE, Rating.of(0.1));

    // inválidos
    assertThrows(
      DomainException.class,
      () -> GameEstado.create(userId, GameId.of(gameId), Estado.PENDIENTE, Rating.of(-1)));
    assertThrows(
      DomainException.class,
      () -> GameEstado.create(userId, GameId.of(gameId), Estado.PENDIENTE, Rating.of(5.1)));
  }
}
