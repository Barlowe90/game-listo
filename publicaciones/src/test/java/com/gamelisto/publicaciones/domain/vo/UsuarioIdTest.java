package com.gamelisto.publicaciones.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UsuarioId - Tests")
class UsuarioIdTest {

  @Test
  @DisplayName("of(valid) debe crear instancia y mantener valor")
  void of_debeCrearConValorValido() {
    UUID value = UUID.randomUUID();
    UsuarioId id = UsuarioId.of(value);
    assertThat(id.value()).isEqualTo(value);
  }

  @Test
  @DisplayName("of(null) debe lanzar DomainException")
  void of_null_debeLanzar() {
    assertThatThrownBy(() -> UsuarioId.of(null)).isInstanceOf(DomainException.class);
  }
}
