package com.gamelisto.social.dominio;

import com.gamelisto.social.dominio.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Amistad - Value Object de dominio")
class AmistadTest {
  @Test
  @DisplayName("debe normalizar el par de ids")
  void debeNormalizarElParDeIds() {
    Amistad ab = Amistad.of("alice", "bob");
    Amistad ba = Amistad.of("bob", "alice");
    assertEquals(ab.userAId(), ba.userAId());
  }

  @Test
  @DisplayName("debe lanzar excepcion con mismos ids")
  void debeLanzarExcepcionConMismosIds() {
    assertThrows(DomainException.class, () -> Amistad.of("a", "a"));
  }

  @Test
  @DisplayName("debe lanzar excepcion con null")
  void debeLanzarExcepcionConNull() {
    assertThrows(DomainException.class, () -> Amistad.of(null, "b"));
  }

  @Test
  @DisplayName("debe lanzar excepcion con vacio")
  void debeLanzarExcepcionConVacio() {
    assertThrows(DomainException.class, () -> Amistad.of("", "b"));
  }
}
