package com.gamelisto.publicaciones.domain.vo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled("Plantilla de test para PublicacionId: deshabilitada hasta implementar el VO")
@DisplayName("PublicacionId - Plantilla de tests")
class PublicacionIdTest {

  @Test
  @DisplayName("of(valid) debe crear instancia y mantener valor")
  void of_debeCrearConValorValido() {
    // TODO: Descomentar y adaptar cuando se implemente PublicacionId
    // UUID value = UUID.randomUUID();
    // PublicacionId id = PublicacionId.of(value);
    // assertThat(id.value()).isEqualTo(value);
  }

  @Test
  @DisplayName("of(null) debe lanzar DomainException")
  void of_null_debeLanzar() {
    // TODO: Descomentar y adaptar cuando se implemente PublicacionId
    // assertThrows(DomainException.class, () -> PublicacionId.of(null));
  }
}
