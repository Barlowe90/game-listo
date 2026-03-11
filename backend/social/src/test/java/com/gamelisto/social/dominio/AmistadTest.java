package com.gamelisto.social.dominio;

import com.gamelisto.social.dominio.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

@DisplayName("Amistad - Value Object de dominio")
class AmistadTest {
  @Test
  @DisplayName("debe normalizar el par de ids")
  void debeNormalizarElParDeIds() {
    UUID a = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    UUID b = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    Amistad ab = Amistad.of(a, b);
    Amistad ba = Amistad.of(b, a);
    assertEquals(ab.userAId(), ba.userAId());
  }

  @Test
  @DisplayName("debe lanzar excepcion con mismos ids")
  void debeLanzarExcepcionConMismosIds() {
    UUID a = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    assertThrows(DomainException.class, () -> Amistad.of(a, a));
  }

  @Test
  @DisplayName("debe lanzar excepcion con null")
  void debeLanzarExcepcionConNull() {
    UUID b = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    assertThrows(DomainException.class, () -> Amistad.of(null, b));
  }

  @Test
  @DisplayName("debe lanzar excepcion con vacio")
  void debeLanzarExcepcionConVacio() {
    // UUID tiene formato; para simular "vacio" usamos null porque UUID no acepta vacíos
    UUID b = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    assertThrows(DomainException.class, () -> Amistad.of(null, b));
  }
}
