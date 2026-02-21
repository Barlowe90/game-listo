package com.gamelist.catalogo.domain.game;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/** Tests para el Value Object GameName. Pruebas de domain puro - sin Spring. */
class GameNameTest {

  @Test
  @DisplayName("Debe crear GameName válido")
  void debeCrearGameNameValido() {
    // Arrange & Act
    GameName name = GameName.of("The Legend of Zelda: Breath of the Wild");

    // Assert
    assertThat(name.value()).isEqualTo("The Legend of Zelda: Breath of the Wild");
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco al inicio y final")
  void debeEliminarEspaciosEnBlanco() {
    // Arrange & Act
    GameName name = GameName.of("  Super Mario Bros  ");

    // Assert
    assertThat(name.value()).isEqualTo("Super Mario Bros");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el nombre es nulo")
  void debeLanzarExcepcionSiNombreNulo() {
    // Act & Assert
    assertThatThrownBy(() -> GameName.of(null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("no puede estar vacío");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el nombre está vacío")
  void debeLanzarExcepcionSiNombreVacio() {
    // Act & Assert
    assertThatThrownBy(() -> GameName.of(""))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("no puede estar vacío");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el nombre solo tiene espacios")
  void debeLanzarExcepcionSiSoloEspacios() {
    // Act & Assert
    assertThatThrownBy(() -> GameName.of("   "))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("no puede estar vacío");
  }

  @Test
  @DisplayName("Debe lanzar excepción si excede 200 caracteres")
  void debeLanzarExcepcionSiExcede200Caracteres() {
    // Arrange
    String nombreLargo = "A".repeat(201);

    // Act & Assert
    assertThatThrownBy(() -> GameName.of(nombreLargo))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("excede los 200 caracteres");
  }

  @Test
  @DisplayName("Debe aceptar nombre con exactamente 200 caracteres")
  void debeAceptar200Caracteres() {
    // Arrange
    String nombre200 = "A".repeat(200);

    // Act
    GameName name = GameName.of(nombre200);

    // Assert
    assertThat(name.value()).hasSize(200);
  }

  @Test
  @DisplayName("Debe ser igual si tienen el mismo valor")
  void debeSerIgualSiMismoValor() {
    // Arrange
    GameName name1 = GameName.of("Dark Souls");
    GameName name2 = GameName.of("Dark Souls");

    // Assert
    assertThat(name1).isEqualTo(name2);
    assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
  }

  @Test
  @DisplayName("No debe ser igual si tienen diferente valor")
  void noDebeSerIgualSiDiferenteValor() {
    // Arrange
    GameName name1 = GameName.of("Dark Souls");
    GameName name2 = GameName.of("Dark Souls II");

    // Assert
    assertThat(name1).isNotEqualTo(name2);
  }
}
