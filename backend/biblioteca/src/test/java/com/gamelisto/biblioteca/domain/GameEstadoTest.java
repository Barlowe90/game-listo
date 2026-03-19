package com.gamelisto.biblioteca.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GameEstado rating validation")
class GameEstadoTest {

  @Test
  @DisplayName("debe aceptar ratings entre 0.0 y 10.0 en saltos de 0.25")
  void ratingValidation() {
    UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    Long gameId = Long.MAX_VALUE;

    GameEstado.create(userId, GameId.of(gameId), Estado.DESEADO, Rating.of(0.0));
    GameEstado.create(userId, GameId.of(gameId), Estado.PENDIENTE, Rating.of(0.25));
    GameEstado.create(userId, GameId.of(gameId), Estado.JUGANDO, Rating.of(7.5));
    GameEstado.create(userId, GameId.of(gameId), Estado.COMPLETADO, Rating.of(9.75));
    GameEstado.create(userId, GameId.of(gameId), Estado.ABANDONADO, Rating.of(10.0));

    assertThrows(
        DomainException.class,
        () -> GameEstado.create(userId, GameId.of(gameId), Estado.PENDIENTE, Rating.of(-0.25)));
    assertThrows(
        DomainException.class,
        () -> GameEstado.create(userId, GameId.of(gameId), Estado.PENDIENTE, Rating.of(0.1)));
    assertThrows(
        DomainException.class,
        () -> GameEstado.create(userId, GameId.of(gameId), Estado.PENDIENTE, Rating.of(10.25)));
  }
}
