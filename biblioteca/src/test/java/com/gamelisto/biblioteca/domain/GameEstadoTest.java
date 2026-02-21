package com.gamelisto.biblioteca.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gamelisto.biblioteca.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GameEstado rating validation")
class GameEstadoTest {

  @Test
  @DisplayName("debe aceptar rating válido 0.0, 0.25, 5.0, 0.1 y rechazar 5.1")
  void ratingValidation() {
    // valores válidos
    GameEstado.create("1", Estado.DESEADO, 0.0);
    GameEstado.create("1", Estado.PENDIENTE, 0.25);
    GameEstado.create("1", Estado.COMPLETADO, 5.0);
    GameEstado.create("1", Estado.PENDIENTE, 0.1);

    // inválidos
    assertThrows(DomainException.class, () -> GameEstado.create("1", Estado.PENDIENTE, -1));
    assertThrows(DomainException.class, () -> GameEstado.create("1", Estado.PENDIENTE, 5.1));
  }
}
