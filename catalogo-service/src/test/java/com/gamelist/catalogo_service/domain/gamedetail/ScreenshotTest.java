package com.gamelist.catalogo_service.domain.gamedetail;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/** Tests para el Value Object Screenshot. */
class ScreenshotTest {

  @Test
  @DisplayName("Debe crear screenshot con URL y dimensiones")
  void debeCrearScreenshotCompleto() {
    // Arrange & Act
    Screenshot screenshot = Screenshot.of("https://example.com/screenshot.jpg", 1920, 1080);

    // Assert
    assertThat(screenshot.url()).isEqualTo("https://example.com/screenshot.jpg");
    assertThat(screenshot.width()).isEqualTo(1920);
    assertThat(screenshot.height()).isEqualTo(1080);
    assertThat(screenshot.hasDimensions()).isTrue();
  }

  @Test
  @DisplayName("Debe crear screenshot solo con URL")
  void debeCrearScreenshotSoloURL() {
    // Arrange & Act
    Screenshot screenshot = Screenshot.of("https://example.com/screenshot.jpg");

    // Assert
    assertThat(screenshot.url()).isEqualTo("https://example.com/screenshot.jpg");
    assertThat(screenshot.width()).isNull();
    assertThat(screenshot.height()).isNull();
    assertThat(screenshot.hasDimensions()).isFalse();
  }

  @Test
  @DisplayName("Debe lanzar excepción si URL es nula")
  void debeLanzarExcepcionSiURLNula() {
    // Act & Assert
    assertThatThrownBy(() -> Screenshot.of(null, 1920, 1080))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("URL del screenshot no puede estar vacía");
  }

  @Test
  @DisplayName("Debe lanzar excepción si URL está vacía")
  void debeLanzarExcepcionSiURLVacia() {
    // Act & Assert
    assertThatThrownBy(() -> Screenshot.of("", 1920, 1080))
        .isInstanceOf(InvalidGameDataException.class);
  }

  @Test
  @DisplayName("Debe lanzar excepción si width es cero o negativo")
  void debeLanzarExcepcionSiWidthInvalido() {
    // Act & Assert
    assertThatThrownBy(() -> Screenshot.of("https://example.com/img.jpg", 0, 1080))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("ancho del screenshot debe ser positivo");

    assertThatThrownBy(() -> Screenshot.of("https://example.com/img.jpg", -100, 1080))
        .isInstanceOf(InvalidGameDataException.class);
  }

  @Test
  @DisplayName("Debe lanzar excepción si height es cero o negativo")
  void debeLanzarExcepcionSiHeightInvalido() {
    // Act & Assert
    assertThatThrownBy(() -> Screenshot.of("https://example.com/img.jpg", 1920, 0))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("altura del screenshot debe ser positiva");
  }

  @Test
  @DisplayName("Dos screenshots con mismos valores deben ser iguales")
  void dosScreenshotsConMismosValoresDebenSerIguales() {
    // Arrange
    Screenshot s1 = Screenshot.of("https://example.com/img.jpg", 1920, 1080);
    Screenshot s2 = Screenshot.of("https://example.com/img.jpg", 1920, 1080);

    // Assert
    assertThat(s1).isEqualTo(s2);
    assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
  }
}
