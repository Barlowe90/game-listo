package com.gamelisto.catalogo.domain.platform;

import com.gamelisto.catalogo.domain.PlatformName;
import com.gamelisto.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/** Tests para el Value Object PlatformName. */
class PlatformNameTest {

  @Test
  @DisplayName("Debe crear PlatformName válido")
  void debeCrearPlatformNameValido() {
    // Arrange & Act
    PlatformName name = PlatformName.of("PlayStation 5");

    // Assert
    assertThat(name.value()).isEqualTo("PlayStation 5");
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco")
  void debeEliminarEspaciosEnBlanco() {
    // Arrange & Act
    PlatformName name = PlatformName.of("  Nintendo Switch  ");

    // Assert
    assertThat(name.value()).isEqualTo("Nintendo Switch");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el nombre es nulo")
  void debeLanzarExcepcionSiNombreNulo() {
    // Act & Assert
    assertThatThrownBy(() -> PlatformName.of(null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("no puede estar vacío");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el nombre está vacío")
  void debeLanzarExcepcionSiNombreVacio() {
    // Act & Assert
    assertThatThrownBy(() -> PlatformName.of("")).isInstanceOf(DomainException.class);
  }

  @Test
  @DisplayName("Debe lanzar excepción si excede 100 caracteres")
  void debeLanzarExcepcionSiExcede100Caracteres() {
    // Arrange
    String nombreLargo = "A".repeat(101);

    // Act & Assert
    assertThatThrownBy(() -> PlatformName.of(nombreLargo))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("excede los 100 caracteres");
  }

  @Test
  @DisplayName("Dos PlatformName con mismo valor deben ser iguales")
  void dosPlatformNameConMismoValorDebenSerIguales() {
    // Arrange
    PlatformName name1 = PlatformName.of("Xbox Series X/S");
    PlatformName name2 = PlatformName.of("Xbox Series X/S");

    // Assert
    assertThat(name1).isEqualTo(name2);
    assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
  }
}
