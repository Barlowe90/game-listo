package com.gamelisto.publicaciones.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GameId - Tests")
class GameIdTest {

  @Test
  @DisplayName("of(valid) debe crear instancia y mantener valor")
  void of_debeCrearConValorValido() {
    GameId id = GameId.of(123L);
    assertThat(id.value()).isEqualTo(123L);
  }

  @Test
  @DisplayName("of(null) debe lanzar DomainException")
  void of_null_debeLanzar() {
    assertThatThrownBy(() -> GameId.of(null)).isInstanceOf(DomainException.class);
  }

  @Test
  @DisplayName("of(<=0) debe lanzar DomainException")
  void of_negativo_debeLanzar() {
    assertThatThrownBy(() -> GameId.of(0L)).isInstanceOf(DomainException.class);
  }
}
