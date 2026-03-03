package com.gamelisto.biblioteca.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NombreListaGame tests")
class NombreListaGameTest {

  @Test
  @DisplayName("debe aceptar nombres con espacios y caracteres válidos")
  void debeAceptarNombresConEspacios() {
    NombreListaGame n = NombreListaGame.of("Mis juegos favoritos");
    assertEquals("Mis juegos favoritos", n.value());
  }

  @Test
  @DisplayName("debe rechazar nombres demasiado cortos o con caracteres inválidos")
  void debeRechazarNombresInvalidos() {
    assertThrows(DomainException.class, () -> NombreListaGame.of("ab"));
    assertThrows(DomainException.class, () -> NombreListaGame.of("!nvalido#"));
  }
}
