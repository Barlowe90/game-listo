package com.gamelist.catalogo.domain.game;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/** Tests para el Value Object Summary. */
class SummaryTest {

  @Test
  @DisplayName("Debe crear Summary válido con texto")
  void debeCrearSummaryValido() {
    // Arrange & Act
    Summary summary = Summary.of("Un juego de aventuras épico");

    // Assert
    assertThat(summary.value()).isEqualTo("Un juego de aventuras épico");
    assertThat(summary.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("Debe crear Summary vacío si se pasa null")
  void debeCrearSummaryVacioConNull() {
    // Arrange & Act
    Summary summary = Summary.of(null);

    // Assert
    assertThat(summary.value()).isNull();
    assertThat(summary.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Debe crear Summary vacío con método empty()")
  void debeCrearSummaryVacioConMetodoEmpty() {
    // Arrange & Act
    Summary summary = Summary.empty();

    // Assert
    assertThat(summary.value()).isNull();
    assertThat(summary.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco")
  void debeEliminarEspaciosEnBlanco() {
    // Arrange & Act
    Summary summary = Summary.of("  Texto con espacios  ");

    // Assert
    assertThat(summary.value()).isEqualTo("Texto con espacios");
  }

  @Test
  @DisplayName("Debe lanzar excepción si excede 1000 caracteres")
  void debeLanzarExcepcionSiExcede1000Caracteres() {
    // Arrange
    String textoLargo = "A".repeat(1001);

    // Act & Assert
    assertThatThrownBy(() -> Summary.of(textoLargo))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("excede los 1000 caracteres");
  }

  @Test
  @DisplayName("Debe aceptar exactamente 1000 caracteres")
  void debeAceptar1000Caracteres() {
    // Arrange
    String texto1000 = "A".repeat(1000);

    // Act
    Summary summary = Summary.of(texto1000);

    // Assert
    assertThat(summary.value()).hasSize(1000);
  }

  @Test
  @DisplayName("Debe ser igual si tienen el mismo valor")
  void debeSerIgualSiMismoValor() {
    // Arrange
    Summary summary1 = Summary.of("Descripción del juego");
    Summary summary2 = Summary.of("Descripción del juego");

    // Assert
    assertThat(summary1).isEqualTo(summary2);
    assertThat(summary1.hashCode()).isEqualTo(summary2.hashCode());
  }

  @Test
  @DisplayName("Dos summaries vacíos deben ser iguales")
  void dosSummariesVaciosDebenSerIguales() {
    // Arrange
    Summary summary1 = Summary.empty();
    Summary summary2 = Summary.of(null);

    // Assert
    assertThat(summary1).isEqualTo(summary2);
  }
}
