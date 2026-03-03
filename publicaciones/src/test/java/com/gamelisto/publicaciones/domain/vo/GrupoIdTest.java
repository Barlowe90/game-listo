package com.gamelisto.publicaciones.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GrupoId - Tests")
class GrupoIdTest {

  @Test
  @DisplayName("of(valid) debe crear instancia y mantener valor")
  void of_debeCrearConValorValido() {
    UUID value = UUID.randomUUID();
    GrupoId id = GrupoId.of(value);
    assertThat(id.value()).isEqualTo(value);
  }

  @Test
  @DisplayName("of(null) debe lanzar DomainException")
  void of_null_debeLanzar() {
    assertThatThrownBy(() -> GrupoId.of(null)).isInstanceOf(DomainException.class);
  }
}
