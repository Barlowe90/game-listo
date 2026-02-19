package com.gamelist.catalogo_service.domain.game;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/** Tests para el Value Object GameId. Pruebas de dominio puro - sin Spring. */
class GameIdTest {

  @Test
  @DisplayName("Debe crear GameId válido con valor positivo")
  void debeCrearGameIdValido() {
    // Arrange & Act
    GameId gameId = GameId.of(12345L);

    // Assert
    assertThat(gameId.value()).isEqualTo(12345L);
  }

  @Test
  @DisplayName("Debe lanzar excepción si el ID es nulo")
  void debeLanzarExcepcionSiIdNulo() {
    // Act & Assert
    assertThatThrownBy(() -> GameId.of(null))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("no puede ser nulo");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el ID es cero")
  void debeLanzarExcepcionSiIdCero() {
    // Act & Assert
    assertThatThrownBy(() -> GameId.of(0L))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("debe ser un número positivo");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el ID es negativo")
  void debeLanzarExcepcionSiIdNegativo() {
    // Act & Assert
    assertThatThrownBy(() -> GameId.of(-100L))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("debe ser un número positivo");
  }

  @Test
  @DisplayName("Debe ser igual si tienen el mismo valor")
  void debeSerIgualSiMismoValor() {
    // Arrange
    GameId id1 = GameId.of(100L);
    GameId id2 = GameId.of(100L);

    // Assert
    assertThat(id1).isEqualTo(id2);
    assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
  }

  @Test
  @DisplayName("No debe ser igual si tienen diferente valor")
  void noDebeSerIgualSiDiferenteValor() {
    // Arrange
    GameId id1 = GameId.of(100L);
    GameId id2 = GameId.of(200L);

    // Assert
    assertThat(id1).isNotEqualTo(id2);
  }

  @Test
  @DisplayName("Debe tener representación toString correcta")
  void debeTenerToStringCorrecto() {
    // Arrange
    GameId gameId = GameId.of(12345L);

    // Act
    String result = gameId.toString();

    // Assert
    assertThat(result).contains("GameId").contains("12345");
  }
}
